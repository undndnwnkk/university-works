function updateGraph(rValue) {
    if (!rValue) return;

    const rDisplay = document.getElementById('check-form:r-value-display');
    if (rDisplay) rDisplay.innerText = rValue;

    const scale = 30;
    const center = 150;

    const rectSize = rValue * scale;
    const rect = document.getElementById('shape-rect');
    if (rect) {
        rect.setAttribute('x', center - rectSize);
        rect.setAttribute('y', center);
        rect.setAttribute('width', rectSize);
        rect.setAttribute('height', rectSize);
    }

    const triLegX = rValue * scale;
    const triLegY = (rValue / 2) * scale;
    const triangle = document.getElementById('shape-triangle');
    if (triangle) {
        const points = `${center},${center} ${center - triLegX},${center} ${center},${center - triLegY}`;
        triangle.setAttribute('points', points);
    }

    const arcRadius = (rValue / 2) * scale;
    const arcPath = document.getElementById('shape-arc');
    if (arcPath) {
        const d = `M ${center},${center} L ${center + arcRadius},${center} A ${arcRadius},${arcRadius} 0 0 1 ${center},${center + arcRadius} Z`;
        arcPath.setAttribute('d', d);
    }

    const points = document.querySelectorAll('.result-point');
    points.forEach(point => {
        const x = parseFloat(point.getAttribute('data-x'));
        const y = parseFloat(point.getAttribute('data-y'));
        const isHit = checkHit(x, y, rValue);
        point.setAttribute('fill', isHit ? 'green' : 'red');
    });
}

function checkHit(x, y, r) {
    if (x <= 0 && y >= 0) {
        return y <= (x / 2 + r / 2);
    }

    if (x <= 0 && y <= 0) {
        return x >= -r && y >= -r;
    }

    if (x >= 0 && y <= 0) {
        return (x * x + y * y) <= (r / 2) * (r / 2);
    }

    return false;
}

function handleGraphClick(event) {
    let rValue;

    if (PF('rSliderWidget')) {
        rValue = PF('rSliderWidget').getValue();
    }

    if (!rValue || rValue <= 0) {
        alert("Пожалуйста, выберите корректное значение R.");
        return;
    }

    const svg = event.currentTarget;
    const rect = svg.getBoundingClientRect();
    const svgX = event.clientX - rect.left;
    const svgY = event.clientY - rect.top;

    const centerX = 150;
    const centerY = 150;
    const scale = 30;

    const mathX = (svgX - centerX) / scale;
    const mathY = (centerY - svgY) / scale;

    document.getElementById('check-form:graph-x').value = mathX.toFixed(3);
    document.getElementById('check-form:graph-y').value = mathY.toFixed(3);

    sendPointFromGraph();
}