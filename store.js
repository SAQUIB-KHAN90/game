import { create } from 'zustand';

const useGameStore = create((set) => ({
  isSpinning: false,
  selectedPrize: null,
  spinWheel: () => {
    set({ isSpinning: true });
    setTimeout(() => {
      const prizes = ["$10", "$50", "Try Again", "$100", "Jackpot!", "Free Spin"];
      const winner = prizes[Math.floor(Math.random() * prizes.length)];
      set({ selectedPrize: winner, isSpinning: false });
    }, 3000);
  },
  resetGame: () => set({ selectedPrize: null })
}));

export default useGameStore;
