document.addEventListener('DOMContentLoaded', () => {
    const yInput = document.querySelector('#y-text');
    const form = document.querySelector('#check-form');
    const grafik = document.querySelector('#graphik');
    const clearButton = document.querySelector('#clear-button');
    const rRadiosContainer = document.querySelector('#r-radios');

    const xValues = [-5, -4, -3, -2, -1, 0, 1, 2, 3];

    rRadiosContainer.addEventListener('change', (e) => {
        const r = e.target.value;
        updateGrafik(r);
    })

    clearButton.addEventListener('click', () => {
        fetch('clear_history', {
            method: 'POST'
        }).then(response => {
            if(response.ok) {
                window.location.reload();
            } else {
                alert("Ошибка в очистке таблицы проверок");
            }
        })
    })

    grafik.addEventListener('click', (e) => {
        const r = document.querySelector('input[name="r_value"]:checked');

        if (!r) {
            alert('Выберите значение R');
            return;
        }

        const rValue = r.value;
        updateGrafik(rValue);

        const rect = grafik.getBoundingClientRect();
        const xFromSvg = e.clientX - rect.left;
        const yFromSvg  = e.clientY - rect.top;

        const mathX = (xFromSvg - 150) / 150 * rValue;
        const mathY = (150 - yFromSvg) / 150 * rValue;

        const x = findClosestX(xValues, mathX);
        document.querySelector('#x-select').value = x;
        yInput.value = mathY.toFixed(3);

        if (validateY()) {
            form.submit();
        }
    })

    form.addEventListener('submit', (e) => {
        if (!validateY()) {
            e.preventDefault();
        }
    })

    const validateY = () => {
        const yValue = yInput.value.trim();

        if (isNaN(yValue) || yValue == "") {
            alert('Пожалуйста, введите число в поле Y');
            return false;
        }

        const yNumber = parseFloat(yValue);

        if (yNumber < -3 || yNumber > 5) {
            alert('Y должен быть в диапазоне от -3 до 5 (включительно)')
            return false;
        }

        return true;
    }
})

const updateGrafik = (r) => {
    const labels = {
        '#label-x-r': r, '#label-x-r-half': r / 2,
        '#label-x-neg-r': -r, '#label-x-neg-r-half': -r / 2,
        '#label-y-r': r, '#label-y-r-half': r / 2,
        '#label-y-neg-r': -r, '#label-y-neg-r-half': -r / 2
    };

    for (const id in labels) {
        document.querySelector(id).textContent = labels[id];
    }
};

const findClosestX = (xValues, val) => {
    let resIndex = 0;
    let minDiff = Math.abs(xValues[0] - val);

    for (let i = 1; i < xValues.length; i++) {
        const currentDiff = Math.abs(xValues[i] - val);
        if (currentDiff < minDiff) {
            minDiff = currentDiff;
            resIndex = i;
        }
    }

    return xValues[resIndex];
}