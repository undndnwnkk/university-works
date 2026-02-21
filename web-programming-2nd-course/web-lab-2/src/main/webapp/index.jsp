<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.weblab.model.CheckResult" %>
<%@ page import="java.util.Collections" %>

<%
    List<CheckResult> results = (List<CheckResult>) session.getAttribute("results");
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Lab 2</title>
    <link rel="stylesheet" href="styles/style.css">
</head>

<body>
    <header class="header">
        <h1>Быков Лев</h1>
        <h1>Группа: Р3219</h1>
        <h1>Вариант: 3384</h1>
    </header>

    <main class="main">
        <div class="main-content">
            <form action="controller" id="check-form" method="post">
                <div class="form-group">
                    <label for="x-select">Выберите X:</label>
                    <select name="x_select_visual" id="x-select" required>
                        <option value="-4">-4</option>
                        <option value="-3">-3</option>
                        <option value="-2">-2</option>
                        <option value="-1">-1</option>
                        <option value="0" selected>0</option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                    </select>
                    <input type="hidden" name="x_value" id="x-hidden-value">
                </div>

                <div class="form-group">
                    <label for="y-text">Введите Y:</label>
                    <input type="text" name="y_value" id="y-text" placeholder="(-3 ... 5)" required>
                </div>

                <div class="form-group" id="r-radios">
                    <label>Выберите R:</label>
                    <div>
                        <input type="radio" name="r_value" value="1" id="r1" required checked><label for="r1">1</label>
                        <input type="radio" name="r_value" value="2" id="r2"><label for="r2">2</label>
                        <input type="radio" name="r_value" value="3" id="r3"><label for="r3">3</label>
                        <input type="radio" name="r_value" value="4" id="r4"><label for="r4">4</label>
                        <input type="radio" name="r_value" value="5" id="r5"><label for="r5">5</label>
                    </div>
                </div>

                <div class="form-group">
                    <button type="submit">Проверить</button>
                    <button type="button" id="clear-button">Очистить таблицу</button>
                </div>

            </form>

            <div class="graphik-container">
                <%@include file="WEB-INF/fragments/graphik.jspf"  %>
            </div>
        </div>
        <table id="results_table" border="1">
            <thead>
                <tr>
                    <td>X</td>
                    <td>Y</td>
                    <td>R</td>
                    <td>Попадание</td>
                    <td>Время проверки</td>
                    <td>Время выполнения</td>
                </tr>
            </thead>

            <tbody>
                <% 
                    if (results != null) {
                        Collections.reverse(results);
                        for (CheckResult result : results) {

                %>
                    <tr>
                        <td><%= result.getX() %></td>
                        <td><%= result.getY()%></td>
                        <td><%= result.getR()%></td>
                        <td><%= result.isHit() ? "<p>Да</p>" : "<p>Нет</p>"%></td>
                        <td><%= result.getCurrentTime()%></td>
                        <td><%= result.getExecutionTime()%></td>
                    </tr>
                <%
                        }

                        Collections.reverse(results);
                    }
                %>
            </tbody>
        </table>

        <script src="js/script.js"></script>
    </main>
</body>
</html>