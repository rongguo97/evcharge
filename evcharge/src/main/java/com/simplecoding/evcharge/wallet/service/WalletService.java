    package com.simplecoding.evcharge.wallet.service;

    import com.simplecoding.evcharge.wallet.entity.Wallet;
    import com.simplecoding.evcharge.wallet.repository.WalletRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    @Service
    @RequiredArgsConstructor
    public class WalletService {

        private final WalletRepository walletRepository;

        /**
         * 특정 회원의 지갑 정보 가져오기 (이메일 기준)
         */
        @Transactional
        public Wallet getOrCreateWallet(String email) {
            return walletRepository.findByEmail(email)
                    .orElseGet(() -> {
                        Wallet newWallet = Wallet.builder()
                                .email(email)
                                .reserveFund(0L) // 적립금 초기화
                                .point(0L)       // 포인트 초기화
                                .build();
                        return walletRepository.save(newWallet);
                    });
        }

        public Wallet getWallet(String email) {
            return walletRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("지갑을 찾을 수 없습니다."));
        }
        /**
         * 적립금 충전 (외부 결제 TOPUP)
         * 결제 금액의 10%를 포인트로 적립하는 로직 포함
         */
        @Transactional
        public void chargeReserveFund(String email, Long amount) {
            if (amount == null || amount <= 0) throw new IllegalArgumentException("충전 금액이 올바르지 않습니다.");

            Wallet wallet = getOrCreateWallet(email);

            // 1. 적립금 충전
            wallet.addReserveFund(amount);

            // 2. 결제 금액의 10% 포인트 적립 (요구사항 반영)
            wallet.earnPoint(amount);

            // Dirty Checking으로 자동 저장됩니다.
        }

        /**
         * 적립금 사용 (예약 시 RESERVE_USAGE)
         */
        @Transactional
        public void spendReserveFund(String email, Long amount) {
            Wallet wallet = getOrCreateWallet(email);
            wallet.subtractReserveFund(amount);
        }

        /**
         * 포인트를 적립금으로 전환
         */
        @Transactional
        public void convertPointToFund(String email, Long amount) {
            Wallet wallet = getOrCreateWallet(email);
            wallet.convertPointToReserveFund(amount);
        }
        @Transactional
        public void useBalance(String email, Long amount) {
            // 1. DB에서 지갑 조회 (필드명이 email 인 것 확인!)
            Wallet wallet = walletRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("지갑을 찾을 수 없습니다. email: " + email));

            // 2. 현재 적립금(reserveFund) 확인 (330,000원 들어있는 곳)
            Long currentFund = wallet.getReserveFund() != null ? wallet.getReserveFund() : 0L;

            if (currentFund < amount) {
                throw new IllegalStateException("적립금이 부족합니다. 충전 후 이용해주세요.");
            }

            // 3. 적립금(reserveFund)에서 차감
            wallet.setReserveFund(currentFund - amount);

            // @Transactional 덕분에 별도의 save 호출 없이도 DB에 반영됩니다.
        }
    }