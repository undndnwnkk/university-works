function handleGraphClick(event, rValue) {
    if (!rValue || rValue <= 0) {
        alert("Пожалуйста, выберите корректное значение R перед тем, как отмечать точку на графике.");
        return;
    }

    const svg = event.currentTarget;
    const svgPoint = svg.createSVGPoint();
    svgPoint.x = event.clientX;
    svgPoint.y = event.clientY;

    const transformedPoint = svgPoint.matrixTransform(svg.getScreenCTM().inverse());
    const svgX = transformedPoint.x;
    const svgY = transformedPoint.y;

    const centerX = 150;
    const centerY = 150;

    const scale = 30;
    const mathX = (svgX - centerX) / scale;
    const mathY = (centerY - svgY) / scale;

    document.getElementById('check-form:graph-x').value = mathX.toFixed(3);
    document.getElementById('check-form:graph-y').value = mathY.toFixed(3);

    sendPointFromGraph();
}