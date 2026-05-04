// ── Configuration ───────────────────────────────────────────
const GameConfig = {
    WINDOW_WIDTH: 1550,
    WINDOW_HEIGHT: 794,
    SKY_COLOR: 'rgb(197, 244, 243)',
    GROUND_COLOR: 'rgb(228, 222, 109)',
    GROUND_HEIGHT: 50,

    GRAVITY: 0.4,
    AIR_RESISTANCE_X: 0.996,
    AIR_RESISTANCE_Y: 0.999,
    GROUND_FRICTION: 0.996,
    BOUNCE_DAMPING: 0.55,
    SPEED_THRESHOLD: 0.1,

    CANNON_BASE_DIAMETER: 100,
    CANNON_BARREL_WIDTH: 300,
    CANNON_WHEEL_SIZE: 100,
    CANNON_WHEEL_COLOR: 'rgb(139, 69, 19)',

    SLIDER_WIDTH: 250,
    SLIDER_TRACK_COLOR: 'gray',
    FIRE_BUTTON_WIDTH: 100,
    FIRE_BUTTON_HEIGHT: 50,
    CLEAR_BUTTON_WIDTH: 100,
    CLEAR_BUTTON_HEIGHT: 40,
    COLOR_BOX_WIDTH: 20,
    MENU_BOX_COLOR: 'rgb(200, 200, 200)',

    C1_X: 90,
    C1_SLIDER_X: 50,
    C1_FIRE_BTN_X: 105,
    C1_FIRE_BTN_Y: 350,
    C1_CLEAR_BTN_X: 320,
    C1_CLEAR_BTN_Y: 25,
    C1_COLOR_SEL_X: 50,
    C1_COLOR_SEL_Y: 75,

    C2_X: 1400,
    C2_SLIDER_X: 1200,
    C2_FIRE_BTN_X: 1275,
    C2_FIRE_BTN_Y: 350,
    C2_CLEAR_BTN_X: 1075,
    C2_CLEAR_BTN_Y: 25,
    C2_COLOR_SEL_X: 1200,
    C2_COLOR_SEL_Y: 75,

    BALL_COLORS: [
        'rgb(85, 85, 85)',
        'rgb(3, 61, 180)',
        'rgb(255, 0, 0)',
        'rgb(27, 137, 60)',
        'rgb(255, 177, 14)',
        'rgb(164, 73, 164)'
    ],
    SPEED_DIVISOR: 4.5,
    CLOUD_COUNT: 4,

    SCORE_DESTROY: 10,
    SCORE_MUTUAL: 5
};

const GameState = {
    RUNNING: 0,
    PAUSED: 1,
    GAME_OVER: 2
};

const Side = {
    LEFT: { directionMultiplier: 1, ballSpawnOffset: -2 },
    RIGHT: { directionMultiplier: -1, ballSpawnOffset: 90 }
};

// ── Audio ───────────────────────────────────────────────────
class SoundManager {
    constructor() {
        this.sounds = {
            'gamestart': 'sounds/gamestart.wav',
            'cannonfire': 'sounds/cannonfire.wav',
            'metal': 'sounds/metal.wav',
            'explosion': 'sounds/pixel_burst.wav'
        };
        this.cache = {};
        for (let key in this.sounds) {
            let audio = new Audio(this.sounds[key]);
            this.cache[key] = audio;
        }
    }
    play(name) {
        if (this.sounds[name]) {
            let audio = new Audio(this.sounds[name]);
            audio.play().catch(() => {});
        }
    }
}

// ── Input ───────────────────────────────────────────────────
class InputHandler {
    constructor(canvas) {
        this.click = false;
        this.dragging = false;
        this.cursorX = 0;
        this.cursorY = 0;

        this.pausePressed = false;
        this.restartPressed = false;
        this.heldKeys = new Set();

        this.p1FirePressed = false;
        this.p1ClearPressed = false;
        this.p1ColorSelect = -1;

        this.p2FirePressed = false;
        this.p2ClearPressed = false;
        this.p2ColorSelect = -1;

        canvas.addEventListener('mousedown', (e) => {
            this.click = true;
            this.dragging = true;
            this.updateCursor(e, canvas);
        });
        window.addEventListener('mouseup', () => {
            this.dragging = false;
        });
        window.addEventListener('mousemove', (e) => {
            this.updateCursor(e, canvas);
        });

        window.addEventListener('keydown', (e) => {
            this.heldKeys.add(e.code);
            switch (e.code) {
                case 'KeyP': this.pausePressed = true; break;
                case 'KeyR': this.restartPressed = true; break;
                case 'KeyF':
                case 'Space': this.p1FirePressed = true; break;
                case 'KeyC': this.p1ClearPressed = true; break;
                case 'Digit1': this.p1ColorSelect = 0; break;
                case 'Digit2': this.p1ColorSelect = 1; break;
                case 'Digit3': this.p1ColorSelect = 2; break;
                case 'Digit4': this.p1ColorSelect = 3; break;
                case 'Digit5': this.p1ColorSelect = 4; break;
                case 'Digit6': this.p1ColorSelect = 5; break;
                case 'Enter': this.p2FirePressed = true; break;
                case 'Backspace': this.p2ClearPressed = true; break;
                case 'Numpad1': this.p2ColorSelect = 0; break;
                case 'Numpad2': this.p2ColorSelect = 1; break;
                case 'Numpad3': this.p2ColorSelect = 2; break;
                case 'Numpad4': this.p2ColorSelect = 3; break;
                case 'Numpad5': this.p2ColorSelect = 4; break;
                case 'Numpad6': this.p2ColorSelect = 5; break;
            }
        });
        window.addEventListener('keyup', (e) => {
            this.heldKeys.delete(e.code);
        });
    }

    updateCursor(e, canvas) {
        const rect = canvas.getBoundingClientRect();
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;
        this.cursorX = (e.clientX - rect.left) * scaleX;
        this.cursorY = (e.clientY - rect.top) * scaleY;
    }

    consumeClick() { this.click = false; }
    isDragging() { return this.dragging; }

    isPausePressed() { let v = this.pausePressed; this.pausePressed = false; return v; }
    isRestartPressed() { let v = this.restartPressed; this.restartPressed = false; return v; }

    isP1FirePressed() { let v = this.p1FirePressed; this.p1FirePressed = false; return v; }
    isP1ClearPressed() { let v = this.p1ClearPressed; this.p1ClearPressed = false; return v; }
    getP1ColorSelect() { let v = this.p1ColorSelect; this.p1ColorSelect = -1; return v; }

    isP1AngleUp()   { return this.heldKeys.has('KeyW'); }
    isP1AngleDown() { return this.heldKeys.has('KeyS'); }
    isP1PowerUp()   { return this.heldKeys.has('KeyD'); }
    isP1PowerDown() { return this.heldKeys.has('KeyA'); }
    isP1SizeUp()    { return this.heldKeys.has('KeyE'); }
    isP1SizeDown()  { return this.heldKeys.has('KeyQ'); }

    isP2FirePressed() { let v = this.p2FirePressed; this.p2FirePressed = false; return v; }
    isP2ClearPressed() { let v = this.p2ClearPressed; this.p2ClearPressed = false; return v; }
    getP2ColorSelect() { let v = this.p2ColorSelect; this.p2ColorSelect = -1; return v; }

    isP2AngleUp()   { return this.heldKeys.has('ArrowUp'); }
    isP2AngleDown() { return this.heldKeys.has('ArrowDown'); }
    isP2PowerUp()   { return this.heldKeys.has('ArrowRight'); }
    isP2PowerDown() { return this.heldKeys.has('ArrowLeft'); }
    isP2SizeUp()    { return this.heldKeys.has('Period'); }
    isP2SizeDown()  { return this.heldKeys.has('Comma'); }
}

// ── Physics & Entities ──────────────────────────────────────
class PhysicsEngine {
    update(ball) {
        ball.x += ball.velocityX + ball.speedX;
        ball.speedX *= GameConfig.AIR_RESISTANCE_X;

        // Wall bounce
        if (ball.x > GameConfig.WINDOW_WIDTH - ball.diameter || ball.x < 0) {
            ball.speedX *= -1;
        }

        // Speed threshold
        if ((ball.speedX < 0 && ball.speedX > -GameConfig.SPEED_THRESHOLD) ||
            (ball.speedX > 0 && ball.speedX < GameConfig.SPEED_THRESHOLD)) {
            ball.speedX = 0;
        }

        // Ground friction
        if (Math.abs(ball.velocity) < GameConfig.SPEED_THRESHOLD && ball.y >= GameConfig.WINDOW_HEIGHT - ball.diameter) {
            ball.speedX *= GameConfig.GROUND_FRICTION;
        }

        // Vertical movement
        ball.velocity *= GameConfig.AIR_RESISTANCE_Y;
        ball.velocity += GameConfig.GRAVITY;
        ball.y += ball.velocityY + ball.velocity;

        // Ground bounce
        if (ball.y + ball.diameter >= GameConfig.WINDOW_HEIGHT) {
            ball.velocity = ball.velocity * -1 + GameConfig.BOUNCE_DAMPING;
            if (ball.velocity + GameConfig.GRAVITY + ball.y + ball.diameter >= GameConfig.WINDOW_HEIGHT) {
                ball.y = GameConfig.WINDOW_HEIGHT - ball.diameter;
            }
        }
    }
}

class CollisionEngine {
    processCollisions(groupA, groupB) {
        let results = [];
        let toRemoveA = new Set();
        let toRemoveB = new Set();

        for (let a of groupA) {
            for (let b of groupB) {
                if (toRemoveA.has(a) || toRemoveB.has(b)) continue;

                if (this.isColliding(a, b)) {
                    let winner = this.determineWinner(a, b);
                    let cx = (a.x + b.x) / 2 + a.diameter/2;
                    let cy = (a.y + b.y) / 2 + a.diameter/2;
                    
                    results.push({ball1: a, ball2: b, cx, cy, winner});

                    if (winner >= 0) toRemoveB.add(b);
                    if (winner <= 0) toRemoveA.add(a);
                }
            }
        }

        for (let i = groupA.length - 1; i >= 0; i--) {
            if (toRemoveA.has(groupA[i])) groupA.splice(i, 1);
        }
        for (let i = groupB.length - 1; i >= 0; i--) {
            if (toRemoveB.has(groupB[i])) groupB.splice(i, 1);
        }

        return results;
    }

    isColliding(a, b) {
        let dx = (a.x + a.diameter/2) - (b.x + b.diameter/2);
        let dy = (a.y + a.diameter/2) - (b.y + b.diameter/2);
        let distance = Math.sqrt(dx*dx + dy*dy);
        let radiusSum = a.diameter/2 + b.diameter/2;
        return distance <= radiusSum;
    }

    determineWinner(a, b) {
        if (a.size > b.size) return 1;
        if (a.size < b.size) return -1;
        if (a.power > b.power) return 1;
        if (a.power < b.power) return -1;
        return 0;
    }
}

class Ball {
    constructor(x, y, diameter, speedX, speedY, color, size, power, speedDivisor) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.speedX = speedX / speedDivisor;
        this.speedY = (speedY / speedDivisor) * -1;
        this.velocityX = this.speedX;
        this.velocityY = 0; // The Java engine tracks velocity separately. Actually wait!
        // In Java: velocity = this.speedY.
        this.velocity = this.speedY;
        this.color = color;
        this.size = size;
        this.power = power;
    }
}

class Cloud {
    constructor() {
        this.x = Math.random() * GameConfig.WINDOW_WIDTH;
        this.y = Math.random() * 400;
        this.width = 30 + Math.random() * 30;
        this.height = this.width / 2;
        this.speed = 0.25 + Math.random() * 0.5;
    }
    draw(ctx) {
        this.x += this.speed;
        if (this.x > GameConfig.WINDOW_WIDTH) {
            this.x = -this.width;
            this.y = Math.random() * 400;
        }
        ctx.fillStyle = 'rgb(204, 204, 204)';
        ctx.beginPath();
        ctx.ellipse(this.x + this.width/2, this.y + this.height/2, this.width/2, this.height/2, 0, 0, Math.PI*2);
        ctx.fill();
    }
}

class FireEffect {
    constructor(x, y) {
        this.x = x;
        this.y = y;
        this.particles = [];
        this.done = false;
        let colors = ['orange', 'red', 'yellow', 'blue', 'white'];
        for (let i = 0; i < 20; i++) {
            this.particles.push({
                x: this.x,
                y: this.y,
                vx: (Math.random() - 0.5) * 10,
                vy: (Math.random() - 0.5) * 10,
                life: 1.0,
                color: colors[Math.floor(Math.random() * colors.length)]
            });
        }
    }
    draw(ctx) {
        let allDead = true;
        for (let p of this.particles) {
            if (p.life > 0) {
                allDead = false;
                p.x += p.vx;
                p.y += p.vy;
                p.life -= 0.025; // 40 frames duration = 1/40 = 0.025
                ctx.fillStyle = p.color;
                ctx.globalAlpha = Math.max(0, p.life);
                ctx.fillRect(p.x, p.y, 4, 4);
                ctx.globalAlpha = 1.0;
            }
        }
        if (allDead) this.done = true;
    }
}

// ── UI Controls ─────────────────────────────────────────────
class SliderInput {
    constructor(x, y, min, max, label, inputHandler) {
        this.x = x;
        this.y = y;
        this.min = min;
        this.max = max;
        this.label = label;
        this.inputHandler = inputHandler;
        
        this.width = GameConfig.SLIDER_WIDTH;
        this.height = 10;
        this.sliderWidth = 10;
        this.sliderHeight = 30;
        this.sliderX = (this.width / 2) - (this.sliderWidth / 2);
        this.sliderGrabbed = false;
    }

    draw(ctx) {
        let cx = this.inputHandler.cursorX;
        let cy = this.inputHandler.cursorY;

        if (this.inputHandler.isDragging() &&
            cx > (this.sliderX - 10) + this.x &&
            cx < this.sliderX + (this.sliderWidth + 10) + this.x &&
            cy > this.y && cy < this.y + this.height) {
            this.sliderGrabbed = true;
        }

        if (!this.inputHandler.isDragging()) {
            this.sliderGrabbed = false;
        }

        if (this.sliderGrabbed && cx > this.x + (this.sliderWidth / 2) && cx < this.x + this.width - 1) {
            this.sliderX = cx - this.x - (this.sliderWidth / 2);
        }

        ctx.fillStyle = GameConfig.SLIDER_TRACK_COLOR;
        ctx.fillRect(this.x, this.y, this.width, this.height);

        ctx.fillStyle = '#000000';
        ctx.fillRect(this.sliderX + this.x, this.y - (this.sliderHeight / 3), this.sliderWidth, this.sliderHeight);

        ctx.fillStyle = '#000000';
        ctx.font = 'bold 24px Calibri';
        let labelWidth = ctx.measureText(this.label).width;
        ctx.fillText(this.label, this.x + (this.width - labelWidth) / 2, this.y - 12);
    }

    getValue() {
        return Math.floor(((this.sliderX + (this.sliderWidth / 2)) / this.width) * (this.max - this.min));
    }

    adjustValue(delta) {
        this.sliderX = Math.max(0, Math.min(this.width - this.sliderWidth, this.sliderX + delta));
    }

    reset() {
        this.sliderX = (this.width / 2) - (this.sliderWidth / 2);
    }
}

class Cannon {
    constructor(side, x, fbx, fby, cbx, cby, csx, csy, soundManager, inputHandler) {
        this.side = side;
        this.cannonX = x;
        this.fireButtonX = fbx;
        this.fireButtonY = fby;
        this.clearButtonX = cbx;
        this.clearButtonY = cby;
        this.colorSelectionX = csx;
        this.colorSelectionY = csy;
        this.soundManager = soundManager;
        this.inputHandler = inputHandler;

        this.diameter = GameConfig.CANNON_BASE_DIAMETER;
        this.width = GameConfig.CANNON_BARREL_WIDTH;
        this.ballX = 0;
        this.ballY = 0;
        this.angle = 0;
        this.size = 0;
        this.power = 0;
        this.colorSelected = GameConfig.BALL_COLORS[0];
        this.balls = [];
        this.score = 0;
    }

    draw(ctx, angle, size, power) {
        this.angle = angle;
        this.size = size;
        this.power = power;

        this.drawBalls(ctx);
        this.drawCannon(ctx);
        this.drawButtons(ctx);
        this.drawColorSelection(ctx);
    }

    drawCannon(ctx) {
        this.diameter = this.size + 50;
        let y = GameConfig.WINDOW_HEIGHT - this.diameter - 50;

        let effectiveAngle = this.angle * this.side.directionMultiplier;
        let cx = this.cannonX;
        let cy = y + this.diameter;

        let xPoly, yPoly;
        if (this.side === Side.LEFT) {
            xPoly = [this.cannonX, this.cannonX + this.width, this.cannonX + this.width, this.cannonX];
        } else {
            xPoly = [this.cannonX, this.cannonX - this.width, this.cannonX - this.width, this.cannonX];
        }
        yPoly = [y, y, y + this.diameter, y + this.diameter];

        for (let i = 0; i < 4; i++) {
            let rotated = this.rotateXY(xPoly[i], yPoly[i], effectiveAngle, cx, cy);
            xPoly[i] = rotated[0];
            yPoly[i] = rotated[1];
        }

        let yShift = y + 100 - yPoly[3];
        for (let i = 0; i < 4; i++) yPoly[i] += yShift;

        // Fix: In the original Java, the offset included "- diameter" to pull the ball perfectly inside the barrel!
        let spawnOffset = (this.side === Side.LEFT) ? (-this.diameter - 2) : (-this.diameter + 90);
        this.ballX = xPoly[1] + (xPoly[2] - xPoly[1]) + spawnOffset;
        this.ballY = yPoly[1];

        ctx.fillStyle = '#000';
        ctx.beginPath();
        ctx.moveTo(xPoly[0], yPoly[0]);
        for(let i=1; i<4; i++) ctx.lineTo(xPoly[i], yPoly[i]);
        ctx.closePath();
        ctx.fill();

        ctx.fillStyle = GameConfig.CANNON_WHEEL_COLOR;
        ctx.beginPath();
        // Wheel drawn at cannonX - 25, WINDOW_HEIGHT - 100 in Java. 
        // In Java: fillOval(cannonX - 25, GameConfig.WINDOW_HEIGHT - 100, CANNON_WHEEL_SIZE, CANNON_WHEEL_SIZE)
        let wRadius = GameConfig.CANNON_WHEEL_SIZE / 2;
        ctx.arc((this.cannonX - 25) + wRadius, (GameConfig.WINDOW_HEIGHT - 100) + wRadius, wRadius, 0, Math.PI*2);
        ctx.fill();
    }

    drawBalls(ctx) {
        let cx = this.inputHandler.cursorX;
        let cy = this.inputHandler.cursorY;

        if (this.inputHandler.click &&
            cx > this.fireButtonX && cx < this.fireButtonX + GameConfig.FIRE_BUTTON_WIDTH &&
            cy > this.fireButtonY && cy < this.fireButtonY + GameConfig.FIRE_BUTTON_HEIGHT) {
            this.fireBall();
            this.inputHandler.consumeClick();
        }

        if (this.inputHandler.click &&
            cx > this.clearButtonX && cx < this.clearButtonX + GameConfig.CLEAR_BUTTON_WIDTH &&
            cy > this.clearButtonY && cy < this.clearButtonY + GameConfig.CLEAR_BUTTON_HEIGHT) {
            this.clearBalls();
            this.inputHandler.consumeClick();
        }

        for (let b of this.balls) {
            ctx.fillStyle = b.color;
            ctx.beginPath();
            ctx.arc(b.x + b.diameter/2, b.y + b.diameter/2, b.diameter/2, 0, Math.PI*2);
            ctx.fill();
        }
    }

    fireBall() {
        let effPower, speedX, speedY;
        if (this.side === Side.LEFT) {
            effPower = this.power * -1;
            let divisor = 157;
            speedX = Math.trunc(effPower - (effPower / divisor) * (this.angle * -1));
            speedY = Math.trunc((effPower / divisor) * (this.angle * -1));
        } else {
            effPower = this.power;
            let divisor = 203;
            speedX = Math.trunc(effPower - (effPower / divisor) * (this.angle * -1));
            speedY = Math.trunc((effPower / divisor) * this.angle);
        }

        this.balls.push(new Ball(this.ballX, this.ballY, this.diameter, speedX, speedY, this.colorSelected, this.size, this.power, GameConfig.SPEED_DIVISOR));
        this.soundManager.play('cannonfire');
        this.soundManager.play('metal');
    }

    drawButtons(ctx) {
        ctx.fillStyle = 'red';
        ctx.fillRect(this.fireButtonX, this.fireButtonY, GameConfig.FIRE_BUTTON_WIDTH, GameConfig.FIRE_BUTTON_HEIGHT);
        ctx.fillStyle = '#000';
        ctx.font = 'bold 36px Calibri';
        ctx.fillText("FIRE", this.fireButtonX + 15, this.fireButtonY + 36);

        ctx.fillStyle = 'cyan';
        ctx.fillRect(this.clearButtonX, this.clearButtonY, GameConfig.CLEAR_BUTTON_WIDTH, GameConfig.CLEAR_BUTTON_HEIGHT);
        ctx.fillStyle = '#000';
        ctx.font = 'bold 24px Calibri';
        ctx.fillText("CLEAR", this.clearButtonX + 18, this.clearButtonY + 28);
    }

    drawColorSelection(ctx) {
        let colors = GameConfig.BALL_COLORS;
        let bw = GameConfig.COLOR_BOX_WIDTH;
        let pad = 10;
        let mw = (bw * 2 * colors.length) + pad * 2;
        let mh = bw + 40;

        ctx.fillStyle = GameConfig.MENU_BOX_COLOR;
        ctx.fillRect(this.colorSelectionX - pad, this.colorSelectionY - pad - 24, mw, mh);

        ctx.fillStyle = '#000';
        ctx.font = 'bold 24px Calibri';
        ctx.fillText("Color", this.colorSelectionX, this.colorSelectionY - 12);

        let cx = this.inputHandler.cursorX;
        let cy = this.inputHandler.cursorY;

        for (let i = 0; i < colors.length; i++) {
            let bx = this.colorSelectionX + bw * i * 2;

            if (colors[i] === this.colorSelected) {
                ctx.fillStyle = '#000';
                ctx.fillRect(bx - 4, this.colorSelectionY - 4, bw + 8, bw + 8);
            }

            ctx.fillStyle = colors[i];
            ctx.fillRect(bx, this.colorSelectionY, bw, bw);

            if (this.inputHandler.click && cx > bx && cx < bx + bw && cy > this.colorSelectionY && cy < this.colorSelectionY + bw) {
                this.colorSelected = colors[i];
                this.inputHandler.consumeClick();
            }
        }
    }

    fireFromKeyboard() { this.fireBall(); }
    clearBalls() { this.balls = []; }
    selectColor(idx) {
        if (idx >= 0 && idx < GameConfig.BALL_COLORS.length) {
            this.colorSelected = GameConfig.BALL_COLORS[idx];
        }
    }

    rotateXY(x, y, angle, cx, cy) {
        let tx = x - cx;
        let ty = y - cy;
        let rad = angle / 100.0;
        let rx = tx * Math.cos(rad) - ty * Math.sin(rad);
        let ry = tx * Math.sin(rad) + ty * Math.cos(rad);
        return [Math.trunc(rx + cx), Math.trunc(ry + cy)];
    }

    reset() {
        this.balls = [];
        this.score = 0;
        this.colorSelected = GameConfig.BALL_COLORS[0];
    }
}

// ── HUD ─────────────────────────────────────────────────────
class HUD {
    drawScores(ctx, s1, s2) {
        ctx.font = 'bold 28px Calibri';
        let p1t = "P1: " + s1;
        ctx.fillStyle = 'rgb(3, 61, 180)';
        ctx.fillText(p1t, GameConfig.WINDOW_WIDTH / 2 - 150, 35);
        ctx.fillStyle = '#000';
        ctx.fillText(" | ", GameConfig.WINDOW_WIDTH / 2 - 20, 35);
        let p2t = "P2: " + s2;
        ctx.fillStyle = 'rgb(180, 30, 30)';
        ctx.fillText(p2t, GameConfig.WINDOW_WIDTH / 2 + 30, 35);
    }

    drawPauseOverlay(ctx) {
        ctx.fillStyle = 'rgba(0,0,0,0.6)';
        ctx.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ctx.fillStyle = '#FFF';
        ctx.font = 'bold 64px Calibri';
        let txt = "PAUSED";
        ctx.fillText(txt, (GameConfig.WINDOW_WIDTH - ctx.measureText(txt).width)/2, GameConfig.WINDOW_HEIGHT/2 - 30);
        ctx.fillStyle = 'rgb(255, 200, 50)';
        ctx.font = 'bold 28px Calibri';
        let sub = "Press P to Resume";
        ctx.fillText(sub, (GameConfig.WINDOW_WIDTH - ctx.measureText(sub).width)/2, GameConfig.WINDOW_HEIGHT/2 + 20);
    }

    drawGameOverOverlay(ctx, s1, s2) {
        ctx.fillStyle = 'rgba(0,0,0,0.6)';
        ctx.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ctx.font = 'bold 64px Calibri';
        let winner;
        if (s1 > s2) {
            ctx.fillStyle = 'rgb(50, 150, 255)';
            winner = "PLAYER 1 WINS!";
        } else if (s2 > s1) {
            ctx.fillStyle = 'rgb(255, 80, 80)';
            winner = "PLAYER 2 WINS!";
        } else {
            ctx.fillStyle = '#FFF';
            winner = "IT'S A TIE!";
        }
        ctx.fillText(winner, (GameConfig.WINDOW_WIDTH - ctx.measureText(winner).width)/2, GameConfig.WINDOW_HEIGHT/2 - 40);
        ctx.font = 'bold 28px Calibri';
        ctx.fillStyle = '#FFF';
        let scores = `Final Score — P1: ${s1} | P2: ${s2}`;
        ctx.fillText(scores, (GameConfig.WINDOW_WIDTH - ctx.measureText(scores).width)/2, GameConfig.WINDOW_HEIGHT/2 + 10);
        ctx.fillStyle = 'rgb(255, 200, 50)';
        let rest = "Press R to Restart";
        ctx.fillText(rest, (GameConfig.WINDOW_WIDTH - ctx.measureText(rest).width)/2, GameConfig.WINDOW_HEIGHT/2 + 50);
    }

    drawControlHints(ctx) {
        ctx.font = '16px Calibri';
        let y = GameConfig.WINDOW_HEIGHT - 8;
        ctx.fillStyle = 'rgb(3, 61, 180)';
        ctx.fillText("P1: W/S Angle | A/D Power | Q/E Size | Space Fire | C Clear | 1-6 Color", 20, y);
        ctx.fillStyle = 'rgb(180, 30, 30)';
        let p2t = "P2: \u2191/\u2193 Angle | \u2190/\u2192 Power | ,/. Size | Enter Fire | Bksp Clear | Num1-6 Color";
        ctx.fillText(p2t, GameConfig.WINDOW_WIDTH - ctx.measureText(p2t).width - 20, y);
        ctx.fillStyle = 'rgb(80, 80, 80)';
        let glob = "P = Pause | R = Restart";
        ctx.fillText(glob, (GameConfig.WINDOW_WIDTH - ctx.measureText(glob).width)/2, y - 18);
    }
}

// ── Game Loop & Coordinator ─────────────────────────────────
class GamePanel {
    constructor(canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');
        this.state = GameState.RUNNING;
        
        this.inputHandler = new InputHandler(canvas);
        this.soundManager = new SoundManager();
        this.physics = new PhysicsEngine();
        this.collisions = new CollisionEngine();
        this.hud = new HUD();

        this.c1 = new Cannon(Side.LEFT, GameConfig.C1_X, GameConfig.C1_FIRE_BTN_X, GameConfig.C1_FIRE_BTN_Y, 
            GameConfig.C1_CLEAR_BTN_X, GameConfig.C1_CLEAR_BTN_Y, GameConfig.C1_COLOR_SEL_X, GameConfig.C1_COLOR_SEL_Y, 
            this.soundManager, this.inputHandler);
            
        this.c2 = new Cannon(Side.RIGHT, GameConfig.C2_X, GameConfig.C2_FIRE_BTN_X, GameConfig.C2_FIRE_BTN_Y, 
            GameConfig.C2_CLEAR_BTN_X, GameConfig.C2_CLEAR_BTN_Y, GameConfig.C2_COLOR_SEL_X, GameConfig.C2_COLOR_SEL_Y, 
            this.soundManager, this.inputHandler);

        this.a1 = new SliderInput(GameConfig.C1_SLIDER_X, 155, 157, 0, "Angle", this.inputHandler);
        this.s1 = new SliderInput(GameConfig.C1_SLIDER_X, 225, 0, 75, "Size", this.inputHandler);
        this.p1 = new SliderInput(GameConfig.C1_SLIDER_X, 295, 150, 0, "Power", this.inputHandler);

        this.a2 = new SliderInput(GameConfig.C2_SLIDER_X, 155, 157, 0, "Angle", this.inputHandler);
        this.s2 = new SliderInput(GameConfig.C2_SLIDER_X, 225, 0, 75, "Size", this.inputHandler);
        this.p2 = new SliderInput(GameConfig.C2_SLIDER_X, 295, 150, 0, "Power", this.inputHandler);

        this.clouds = [];
        for(let i=0; i<GameConfig.CLOUD_COUNT; i++) this.clouds.push(new Cloud());
        
        this.fireEffects = [];
        this.started = false;

        document.body.addEventListener('click', () => {
            if(!this.started) {
                this.soundManager.play('gamestart');
                this.started = true;
            }
        }, {once: true});
    }

    update() {
        if (this.inputHandler.isPausePressed()) {
            this.state = (this.state === GameState.RUNNING) ? GameState.PAUSED : GameState.RUNNING;
        }
        if (this.inputHandler.isRestartPressed()) {
            this.c1.reset();
            this.c2.reset();
            this.fireEffects = [];
            this.state = GameState.RUNNING;
            this.a1.reset(); this.s1.reset(); this.p1.reset();
            this.a2.reset(); this.s2.reset(); this.p2.reset();
        }

        if (this.state !== GameState.RUNNING) return;

        let step = 3;
        if (this.inputHandler.isP1AngleUp()) this.a1.adjustValue(step);
        if (this.inputHandler.isP1AngleDown()) this.a1.adjustValue(-step);
        if (this.inputHandler.isP1PowerUp()) this.p1.adjustValue(step);
        if (this.inputHandler.isP1PowerDown()) this.p1.adjustValue(-step);
        if (this.inputHandler.isP1SizeUp()) this.s1.adjustValue(step);
        if (this.inputHandler.isP1SizeDown()) this.s1.adjustValue(-step);

        if (this.inputHandler.isP1FirePressed()) this.c1.fireFromKeyboard();
        if (this.inputHandler.isP1ClearPressed()) this.c1.clearBalls();
        let c1c = this.inputHandler.getP1ColorSelect();
        if (c1c >= 0) this.c1.selectColor(c1c);

        if (this.inputHandler.isP2AngleUp()) this.a2.adjustValue(step);
        if (this.inputHandler.isP2AngleDown()) this.a2.adjustValue(-step);
        if (this.inputHandler.isP2PowerUp()) this.p2.adjustValue(step);
        if (this.inputHandler.isP2PowerDown()) this.p2.adjustValue(-step);
        if (this.inputHandler.isP2SizeUp()) this.s2.adjustValue(step);
        if (this.inputHandler.isP2SizeDown()) this.s2.adjustValue(-step);

        if (this.inputHandler.isP2FirePressed()) this.c2.fireFromKeyboard();
        if (this.inputHandler.isP2ClearPressed()) this.c2.clearBalls();
        let c2c = this.inputHandler.getP2ColorSelect();
        if (c2c >= 0) this.c2.selectColor(c2c);

        for (let b of this.c1.balls) this.physics.update(b);
        for (let b of this.c2.balls) this.physics.update(b);

        let cols = this.collisions.processCollisions(this.c1.balls, this.c2.balls);
        for (let r of cols) {
            this.fireEffects.push(new FireEffect(r.cx, r.cy));
            this.soundManager.play('explosion');
            if (r.winner > 0) this.c1.addScore(GameConfig.SCORE_DESTROY);
            else if (r.winner < 0) this.c2.addScore(GameConfig.SCORE_DESTROY);
            else {
                this.c1.addScore(GameConfig.SCORE_MUTUAL);
                this.c2.addScore(GameConfig.SCORE_MUTUAL);
            }
        }
    }

    render() {
        let ctx = this.ctx;
        ctx.fillStyle = GameConfig.SKY_COLOR;
        ctx.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        ctx.fillStyle = GameConfig.GROUND_COLOR;
        ctx.fillRect(0, GameConfig.WINDOW_HEIGHT - GameConfig.GROUND_HEIGHT, GameConfig.WINDOW_WIDTH, GameConfig.GROUND_HEIGHT);

        for (let c of this.clouds) c.draw(ctx);

        this.c1.draw(ctx, this.a1.getValue(), this.s1.getValue(), this.p1.getValue());
        this.c2.draw(ctx, this.a2.getValue(), this.s2.getValue(), this.p2.getValue());

        for (let i = this.fireEffects.length - 1; i >= 0; i--) {
            let f = this.fireEffects[i];
            f.draw(ctx);
            if (f.done) this.fireEffects.splice(i, 1);
        }

        this.a1.draw(ctx); this.s1.draw(ctx); this.p1.draw(ctx);
        this.a2.draw(ctx); this.s2.draw(ctx); this.p2.draw(ctx);

        this.hud.drawScores(ctx, this.c1.score, this.c2.score);
        this.hud.drawControlHints(ctx);

        if (this.state === GameState.PAUSED) this.hud.drawPauseOverlay(ctx);
        else if (this.state === GameState.GAME_OVER) this.hud.drawGameOverOverlay(ctx, this.c1.score, this.c2.score);
    }
}

export { GamePanel, GameConfig };
