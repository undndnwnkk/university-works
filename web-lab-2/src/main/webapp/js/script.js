document.addEventListener('DOMContentLoaded', () => {
    const xSelect = document.querySelector('#x-select');
    const xHiddenInput = document.querySelector('#x-hidden-value');
    const yInput = document.querySelector('#y-text');
    const form = document.querySelector('#check-form');
    const grafik = document.querySelector('#graphik');
    const rRadiosContainer = document.querySelector('#r-radios');
    const clearButton = document.querySelector('#clear-button');

    const MAX_R = 5;
    const PIXELS_PER_UNIT = 150 / MAX_R;

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
        redrawShapes(e.target.value);
    });

    const initialR = document.querySelector('input[name="r_value"]:checked');
    if (initialR) {
        redrawShapes(initialR.value);
    }

    const syncXValue = () => {
        xHiddenInput.value = xSelect.value;
    };
    xSelect.addEventListener('change', syncXValue);
    syncXValue();

    grafik.addEventListener('click', (e) => {
        const rRadio = document.querySelector('input[name="r_value"]:checked');
        if (!rRadio) { alert('Выберите значение R'); return; }

        const rect = grafik.getBoundingClientRect();
        const svgX = e.clientX - rect.left;
        const svgY = e.clientY - rect.top;

        const mathX = (svgX - 150) / PIXELS_PER_UNIT;
        const mathY = (150 - svgY) / PIXELS_PER_UNIT;

        xHiddenInput.value = mathX.toFixed(3);
        yInput.value = mathY.toFixed(3);

        if (validateY()) {
            form.submit();
        }
    });

    form.addEventListener('submit', (e) => {
        if (xHiddenInput.value === "") {
            syncXValue();
        }
        if (!validateY()) {
            e.preventDefault();
        }
    });

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
});