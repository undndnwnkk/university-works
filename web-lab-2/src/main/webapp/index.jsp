<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.weblab.model.CheckResult" %>
<%@ page import="java.util.Collections" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Lab 2</title>
</head>

<body>
    <header class="header">
        <h1>Быков Лев</h1>
        <h1>Группа: Р3219</h1>
        <h1>Вариант: 3384</h1>
    </header>

    <main class="main">
        <form action="controller" id="check-form" method="post">
            <div class="form-content">
                <label for="x-select">Выберите X</label>
                <select name="x_value" id="x-select" required>
                    <option value="-4">-4</option>
                    <option value="-3">-3</option>
                    <option value="-2">-2</option>
                    <option value="-1">-1</option>
                    <option value="0">0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                </select>
            </div>

            <div class="form-content">
                <label for="y-text">Выберите Y</label>
                <input type="text" name="y_value" id="y-text" placeholder="{-3, ..., 5}" required>
            </div>

            <div class="form-content">
                <label>Выберите R:</label>
                <button type="submit" name="r_value" value="1">1</button>
                <button type="submit" name="r_value" value="2">2</button>
                <button type="submit" name="r_value" value="3">3</button>
                <button type="submit" name="r_value" value="4">4</button>
                <button type="submit" name="r_value" value="5">5</button>
            </div>
        </form>

        <div class="graphik-container">
            <p>Todo graphik</p>
        </div>

        <hr>

        <h2>История проверок</h2>
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
                    List<CheckResult> results = (List<CheckResult>) session.getAttribute("results");
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