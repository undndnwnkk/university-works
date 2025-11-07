document.addEventListener('DOMContentLoaded', () => {
    const xSelect = document.querySelector('#x-select');
    const yInput = document.querySelector('#y-text');
    const form = document.querySelector('#check-form');
    const grafik = document.querySelector('#graphik');
    const rRadiosContainer = document.querySelector('#r-radios');
    const clearButton = document.querySelector('#clear-button');

    const MAX_R = 5;
    const PIXELS_PER_UNIT = 150 / MAX_R;

    const checkHit = (x, y, r) => {
        if (x >= 0 && y >= 0) return (x * x + y * y) <= ((r / 2) * (r / 2));
        if (x >= 0 && y <= 0) return x <= r && y >= -r;
        if (x <= 0 && y <= 0) return y >= (-x - r);
        return false;
    };

    const updatePointsColor = (r) => {
        const points = document.querySelectorAll('.result-point');
        points.forEach(point => {
            const x = parseFloat(point.dataset.mathX);
            const y = parseFloat(point.dataset.mathY);
            point.setAttribute('fill', checkHit(x, y, r) ? 'green' : 'red');
        });
    };

    const redrawShapes = (r) => {
        if (!r) return;
        const rInPixels = r * PIXELS_PER_UNIT;
        const rHalfInPixels = rInPixels / 2;
        const shapeCircle = document.querySelector('#shape-circle');
        const shapeRectangle = document.querySelector('#shape-rectangle');
        const shapeTriangle = document.querySelector('#shape-triangle');
        shapeCircle.setAttribute('d', `M 150 150 L 150 ${150 - rHalfInPixels} A ${rHalfInPixels} ${rHalfInPixels} 0 0 1 ${150 + rHalfInPixels} 150 Z`);
        shapeRectangle.setAttribute('x', 150);
        shapeRectangle.setAttribute('y', 150);
        shapeRectangle.setAttribute('width', rInPixels);
        shapeRectangle.setAttribute('height', rInPixels);
        shapeTriangle.setAttribute('d', `M 150 150 L ${150 - rInPixels} 150 L 150 ${150 + rInPixels} Z`);
    };

    rRadiosContainer.addEventListener('change', (e) => {
        const newR = e.target.value;
        redrawShapes(newR);
        updatePointsColor(newR);
    });

    const initialR = document.querySelector('input[name="r_value"]:checked');
    if (initialR) {
        redrawShapes(initialR.value);
    }

    const validateY = () => {
        const yValue = yInput.value.trim().replace(',', '.');
        yInput.value = yValue;
        if (isNaN(yValue) || yValue === "") {
            alert('Введите число в Y');
            return false;
        }
        const yNumber = parseFloat(yValue);
        if (yNumber <= -3 || yNumber >= 5) {
            alert('Y должен быть в диапазоне (-3 ... 5)');
            return false;
        }
        return true;
    };

    clearButton.addEventListener('click', () => {
        fetch('clear_history', { method: 'POST' }).then(response => {
            if (response.ok) window.location.reload();
            else alert("Ошибка в очистке таблицы");
        });
    });

    const sendCheckRequest = (x, y, r) => {
        const data = {
            x_value: x,
            y_value: y,
            r_value: r
        };

        fetch('controller', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    alert("Данные не полетели на сервер");
                    console.error(`Status ${response.status}`);
                    response.text().then(text => {
                        console.error("Response body: " + text);
                    })
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert("Сервак сломался");
            });
    };

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        if (!validateY()) {
            return;
        }

        const x = parseFloat(xSelect.value);
        const y = parseFloat(yInput.value.trim().replace(',', '.'));
        const r = parseFloat(document.querySelector('input[name="r_value"]:checked').value);

        sendCheckRequest(x, y, r);
    });

    grafik.addEventListener('click', (e) => {
        const rRadio = document.querySelector('input[name="r_value"]:checked');
        if (!rRadio) {
            alert('Выберите значение R');
            return;
        }

        const rect = grafik.getBoundingClientRect();
        const svgX = e.clientX - rect.left;
        const svgY = e.clientY - rect.top;

        const mathX = parseFloat(((svgX - 150) / PIXELS_PER_UNIT).toFixed(3));
        const mathY = parseFloat(((150 - svgY) / PIXELS_PER_UNIT).toFixed(3));
        const mathR = parseFloat(rRadio.value);

        sendCheckRequest(mathX, mathY, mathR);
    });
});