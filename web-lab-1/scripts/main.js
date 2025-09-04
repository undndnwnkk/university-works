const yInput = document.querySelector(".y");
const xInput = document.querySelector(".x");
const rRadioButtons = document.querySelectorAll('.r-radio');
const form = document.querySelector(".input-form");
const errorOutput = document.querySelector(".form-error");
const tbody = document.querySelector(".history-body");

const validateYInput = () => {
    const inputValue = yInput.value.trim();

    if(inputValue === '') {
        alert("Поле Y не может быть пустым");
        return false;
    }

    let isNumber = true;
    let startIndex = 0;

    if(inputValue[0] == '-') {
        startIndex = 1;

        if(inputValue.length == 1) {
            isNumber = false;
        }
    }

    for(let i = startIndex; i < inputValue.length; i++) {
        const currentLetter = inputValue[i];
        if(currentLetter < '0' || currentLetter > '9') {
            isNumber = false;
            break;
        }
    }

    if (isNumber == false) {
        alert("Пишите в поле Y только числа");
        return false;
    } 

    if(parseInt(inputValue) < -5 || parseInt(inputValue) > 3) {
        alert("Напишите число в поле Y в промежутке {-5, ..., 3}");
        return false;
    }

    return true;
}

const validateRInput = () => {
    let isSelected = false;
    rRadioButtons.forEach(button => {
        if(button.checked) {
            isSelected = true;
        }
    })

    if (!isSelected) {
        alert("Выберите значение R");
        return false;
    }

    return true;
}

const renderHistoryTable = (data) => {
    tbody.innerHTML = "";
    data.forEach((row, idx) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${idx + 1}</td>
            <td>${row.now ?? ""}</td>
            <td>${row.x}</td>
            <td>${row.y}</td>
            <td>${row.r}</td>
            <td>${row.hit ? "да" : "нет"}</td>
            <td>${row.elapsedMs ?? ""}</td>
        `;
        tbody.prepend(tr)
    })
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  if (validateYInput() && validateRInput()) {
    const dataFromForm = {
      x: xInput.value,
      y: yInput.value,
      r: document.querySelector("input[name='r']:checked")?.value
    };

    try {
      const response = await fetch(form.action, {
        method: "POST",
        headers: {
            "Content-Type": "application/json" 
        },
        body: JSON.stringify(dataFromForm)
      });

      let data;
      const text = await response.text();
      try { 
        data = JSON.parse(text); 
    } catch { 
        data = null; 
    }

      if (!data) {
        console.log("Ответ сервера (HTML):", text);
        return;
      }

      if (!data.ok) {
        if (errorOutput) 
            errorOutput.textContent = data.error || "Ошибка";
        } else {
            if (errorOutput) 
                errorOutput.textContent = "";
            if (Array.isArray(data.history)) 
                renderHistoryTable(data.history);
        }
    } catch (error) {
      console.error(error);
      if (errorOutput) errorOutput.textContent = "Сеть/сервер недоступны";
    }
  } else {
    alert("Произошла ошибка, проверьте корректность ввода");
  }
});
