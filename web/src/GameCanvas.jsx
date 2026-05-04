import React, { useEffect, useRef } from 'react';
import { GamePanel, GameConfig } from './gameLogic';

export default function GameCanvas({ onExit }) {
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

    const handleKeyDown = (e) => {
        if (e.key === 'Escape') onExit();
    };
    window.addEventListener('keydown', handleKeyDown);

    return () => {
      cancelAnimationFrame(animationFrameId);
      window.removeEventListener('keydown', handleKeyDown);
      panel.destroy();
    };
  }, [onExit]);

  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh',
      backgroundColor: '#000', zIndex: 9999,
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center'
    }}>
      <div style={{ position: 'absolute', top: '10px', left: '10px', zIndex: 10000 }}>
        <button onClick={onExit} style={{
          padding: '10px 20px', backgroundColor: 'red', color: 'white',
          border: 'none', borderRadius: '5px', fontWeight: 'bold', cursor: 'pointer',
          fontFamily: 'Calibri, sans-serif', fontSize: '18px',
          boxShadow: '0 4px 6px rgba(0,0,0,0.5)'
        }}>
          Exit Game (Esc)
        </button>
      </div>
      <div style={{ 
        boxShadow: '0 0 50px rgba(255,255,255,0.2)', 
        borderRadius: '8px', overflow: 'hidden', 
        display: 'flex', justifyContent: 'center' 
      }}>
        <canvas 
          ref={canvasRef} 
          width={GameConfig.WINDOW_WIDTH} 
          height={GameConfig.WINDOW_HEIGHT} 
          style={{ width: '100%', maxWidth: '100vw', maxHeight: '100vh', objectFit: 'contain' }}
        />
      </div>
    </div>
  );
}
