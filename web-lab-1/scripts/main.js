const yInput = document.querySelector(".y");
const rRadioButtons = document.querySelectorAll('.r-radio');
const form = document.querySelector(".input-form");

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

form.addEventListener("submit", (e) => {
    e.preventDefault();

    if (validateYInput() && validateRInput()) {
        form.submit();
    } else {
        alert("Произошла ошибка, проверьте корректность ввода")
    }
})