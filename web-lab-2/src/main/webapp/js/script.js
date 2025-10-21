document.addEventListener('DOMContentLoaded', () => {
    const yInput = document.querySelector('#y-text');
    const form = document.querySelector('#check-form');

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