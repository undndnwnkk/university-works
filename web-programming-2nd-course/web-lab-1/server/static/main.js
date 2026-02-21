document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('.input-form');
    const formError = document.querySelector('.form-error');
    const historyBody = document.querySelector('.history-body');
    const clearHistoryButton = document.querySelector('.clear-storage');

    let historyData = [];

    const addRowToTable = (data) => {
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
    };

    const showHistory = (data) => {
        historyBody.innerHTML = '';
        data.forEach(addRowToTable);
    };

    const savedHistory = localStorage.getItem('history');
    if (savedHistory) {
        try {
            historyData = JSON.parse(savedHistory);
            showHistory(historyData);
        } catch (e) {
            console.error('Ошибка загрузки истории: ' + e.message);
            localStorage.removeItem('history');
        }
    }

    window.addEventListener('beforeunload', () => {
        localStorage.setItem('history', JSON.stringify(historyData));
    });

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        formError.textContent = '';

        const xRaw = document.querySelector('.x').value;
        const yRaw = document.querySelector('.y').value;
        const rRaw = document.querySelector('input[name="r"]:checked')?.value;

        const x = Number(xRaw);
        const y = Number(yRaw);
        const r = Number(rRaw);

        if (!Number.isFinite(y) || y < -5 || y > 3) {
            formError.textContent = 'Y должен быть числом в диапазоне (-5 ... 3).';
            return;
        }
        if (!Number.isFinite(x) || x < -3 || x > 5) {
            formError.textContent = 'X должен быть числом в диапазоне {-3, ..., 5}';
            return;
        }
        if (!Number.isFinite(r)) {
            formError.textContent = 'Пожалуйста, выберите значение R.';
            return;
        }

        const body = JSON.stringify({ x, y, r });

        try {
            const response = await fetch('/calculate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body
            });

            const text = await response.text();
            console.log(response.headers.get('Content-Type'));
            console.log(text);
            let data;
            try { data = JSON.parse(text); }
            catch { data = { error: text || 'Invalid JSON' }; }

            if (!response.ok) {
                throw new Error(data.error || `HTTP ${response.status}`);
            }

            historyData.push(data);
            addRowToTable(data);
            localStorage.setItem('history', JSON.stringify(historyData));
        } catch (error) {
            formError.textContent = `Ошибка: ${error.message}`;
            console.error('Fetch error:', error);
        }
    });

    clearHistoryButton.addEventListener('click', () => {
        historyData = [];
        localStorage.clear();
        location.reload();
    });
});
