import React, { useState } from 'react';
import { Download, Code2, Play } from 'lucide-react';
import GameCanvas from './GameCanvas';
import logo from './assets/logo.jpg';

function App() {
  const [isPlaying, setIsPlaying] = useState(false);

  return (
    <>
      <main>
        <section className="hero">
          <img src={logo} alt="Cannon Battle Logo" style={{ width: '150px', height: '150px', objectFit: 'contain', marginBottom: '1rem', filter: 'drop-shadow(0 0 20px rgba(255, 255, 255, 0.5))', borderRadius: '20px' }} />
          <h1 className="title-glow">CANNON BATTLE</h1>
          <p className="subtitle">
            A premium local multiplayer artillery game with real-time physics,
            collision detection, and particle effects. Experience the modern HTML5 port, 
            or download the original Java AWT classic.
          </p>
        </section>

        <section style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
          <button onClick={() => setIsPlaying(true)} style={{
            display: 'flex', alignItems: 'center', gap: '10px',
            padding: '20px 40px', fontSize: '24px', fontWeight: 'bold',
            backgroundColor: 'var(--accent)', color: 'white', border: 'none',
            borderRadius: '50px', cursor: 'pointer', fontFamily: 'Outfit, sans-serif',
            boxShadow: '0 0 30px var(--accent-glow)', transition: 'transform 0.2s ease'
          }} onMouseOver={(e) => e.currentTarget.style.transform = 'scale(1.05)'} 
             onMouseOut={(e) => e.currentTarget.style.transform = 'scale(1)'}>
            <Play size={28} fill="white" />
            PLAY NOW
          </button>
        </section>

        {isPlaying && <GameCanvas onExit={() => setIsPlaying(false)} />}

        <section className="cta-section">
          <h2 className="cta-title">Want the Classic Experience?</h2>
          <p className="subtitle" style={{ margin: '0 auto 1.5rem' }}>
            This game was originally built from scratch as a monolithic Java AWT application
            and fully refactored into a modular production-ready system. 
            View the source code or download the standalone executable.
          </p>
          
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
            <a href="https://github.com/Siddharthpo03/Java-Cannon-Game-Simulation/releases/latest" 
               className="cta-button" target="_blank" rel="noreferrer">
              <Download size={20} />
              Download Java Executable
            </a>
            
            <a href="https://github.com/Siddharthpo03/Java-Cannon-Game-Simulation" 
               className="cta-button" style={{ background: 'var(--accent)', borderColor: 'var(--accent)' }} 
               target="_blank" rel="noreferrer">
              <Code2 size={20} />
              View Java Source Code
            </a>
          </div>
        </section>
      </main>

      <footer style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
        <p>Built by Siddharthpo03. Open source under the MIT License.</p>
      </footer>
    </>
  );
}

export default App;
