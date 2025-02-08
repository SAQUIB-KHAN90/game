import { useEffect, useRef } from "react";
import useGameStore from "../store/gameStore";
import { motion } from "framer-motion";

const prizes = ["$10", "$50", "Try Again", "$100", "Jackpot!", "Free Spin"];

export default function SpinningWheel() {
  const { isSpinning, selectedPrize, spinWheel } = useGameStore();
  const wheelRef = useRef(null);

  useEffect(() => {
    if (isSpinning) {
      const randomRotation = 1800 + Math.floor(Math.random() * 360);
      wheelRef.current.style.transform = `rotate(${randomRotation}deg)`;
    }
  }, [isSpinning]);

  return (
    <div className="flex flex-col items-center justify-center">
      <div className="relative">
        <motion.div
          ref={wheelRef}
          className="w-64 h-64 rounded-full border-8 border-gray-300 flex items-center justify-center"
          animate={{ rotate: isSpinning ? 1800 : 0 }}
          transition={{ duration: 3, ease: "easeOut" }}
        >
          <div className="grid grid-cols-2 gap-2 text-center">
            {prizes.map((prize, index) => (
              <div
                key={index}
                className="w-28 h-28 bg-blue-500 text-white rounded-full flex items-center justify-center text-lg font-bold"
              >
                {prize}
              </div>
            ))}
          </div>
        </motion.div>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-red-500 text-white px-4 py-2 rounded-full shadow-lg font-bold">
          Spin
        </div>
      </div>

      <button
        className="mt-6 px-4 py-2 bg-green-500 text-white rounded-lg shadow-md"
        onClick={spinWheel}
        disabled={isSpinning}
      >
        {isSpinning ? "Spinning..." : "Spin the Wheel"}
      </button>

      {selectedPrize && (
        <div className="mt-4 p-4 bg-yellow-300 rounded-lg shadow-md text-lg font-bold">
          🎉 You won: {selectedPrize}!
        </div>
      )}
    </div>
  );
}



import SpinningWheel from "../components/SpinningWheel";
import useGameStore from "../store/gameStore";

export default function Home() {
  const { resetGame, selectedPrize } = useGameStore();

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-3xl font-bold mb-4">🎡 Spinning Wheel Game</h1>
      <SpinningWheel />

      {selectedPrize && (
        <button
          className="mt-4 px-4 py-2 bg-red-500 text-white rounded-lg shadow-md"
          onClick={resetGame}
        >
          Play Again
        </button>
      )}
    </div>
  );
}
