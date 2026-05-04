import React, { useEffect, useRef } from 'react';
import { GamePanel } from './gameLogic';

export default function GameCanvas() {
  const canvasRef = useRef(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const panel = new GamePanel(canvas);
    let animationFrameId;

    const renderLoop = () => {
      panel.update();
      panel.render();
      if (panel.inputHandler.click) panel.inputHandler.consumeClick();
      animationFrameId = requestAnimationFrame(renderLoop);
    };

    renderLoop();

    return () => {
      cancelAnimationFrame(animationFrameId);
    };
  }, []);

  return (
    <div className="game-wrapper">
      <div className="glass-panel">
        <canvas ref={canvasRef} width={1000} height={650} />
      </div>
    </div>
  );
}
