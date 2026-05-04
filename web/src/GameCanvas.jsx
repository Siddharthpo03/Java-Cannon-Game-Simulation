import React, { useEffect, useRef } from 'react';
import { GamePanel, GameConfig } from './gameLogic';

export default function GameCanvas() {
  const canvasRef = useRef(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const panel = new GamePanel(canvas);
    let animationFrameId;

    let lastTime = performance.now();
    let accumulator = 0;
    const step = 1000 / 60; // 60 FPS fixed step matching Java GameLoop
    
    const renderLoop = (time) => {
      let dt = time - lastTime;
      lastTime = time;
      accumulator += dt;
      
      // Prevent "spiral of death" if browser tab goes to background
      if (accumulator > 200) accumulator = step;
      
      while (accumulator >= step) {
        panel.update();
        accumulator -= step;
      }
      
      panel.render();
      if (panel.inputHandler.click) panel.inputHandler.consumeClick();
      animationFrameId = requestAnimationFrame(renderLoop);
    };

    animationFrameId = requestAnimationFrame(renderLoop);

    return () => {
      cancelAnimationFrame(animationFrameId);
    };
  }, []);

  return (
    <div className="game-wrapper">
      <div className="glass-panel" style={{ width: '100%', maxWidth: '100vw', overflow: 'hidden', display: 'flex', justifyContent: 'center' }}>
        <canvas 
          ref={canvasRef} 
          width={GameConfig.WINDOW_WIDTH} 
          height={GameConfig.WINDOW_HEIGHT} 
          style={{ width: '100%', maxWidth: `${GameConfig.WINDOW_WIDTH}px`, height: 'auto' }}
        />
      </div>
    </div>
  );
}
