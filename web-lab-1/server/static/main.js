document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('.input-form');
    const formError = document.querySelector('.form-error');
    const historyBody = document.querySelector('.history-body');

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        formError.textContent = '';

        const formData = new FormData(form);
        const xValue = formData.get('x');
        const yValue = formData.get('y').replace(',', '.');
        const rValue = formData.get('r');

        if (!yValue || isNaN(yValue) || parseFloat(yValue) < -5 || parseFloat(yValue) > 3) {
            formError.textContent = 'Y должен быть числом в диапазоне (-5 ... 3).';
            return;
        }

        if (rValue === null) {
            formError.textContent = 'Пожалуйста, выберите значение R.';
            return;
        }

        const body = {
            "x": xValue,
            "y": yValue,
            "r": rValue
        };

        const jsonBody = JSON.stringify(body);


        try {
            const response = await fetch('/calculate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: jsonBody
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || `HTTP error! status: ${response.status}`);
            }

            const newRow = historyBody.insertRow(-1);
            newRow.innerHTML = `
                <td><p>${data.number}</p></td>
                <td><p>${data.time}</p></td>
                <td><p>${data.x}</p></td>
                <td><p>${data.y}</p></td>
                <td><p>${data.r}</p></td>
                <td><p>${data.hit ? 'Да' : 'Нет'}</p></td>
                <td><p>${data.elapsedMs}</p></td>
            `;

        } catch (error) {
            formError.textContent = `Ошибка: ${error.message}`;
            console.error('Fetch error:', error);
        }
    });
});