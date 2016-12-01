<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Films</title>
</head>
<body>    
<h1>
	<c:choose>
	    <c:when test="${userId==null}">
	        Aucune note
	    </c:when>    
	    <c:otherwise>
	        Notes de l'utilisateur ${userId} pour les films :
	    </c:otherwise>
	</c:choose>
</h1>
<ul>
	<c:forEach items="${ratings}" var="r">
		<li>
                    <span>${r.movie.title}</span>
                    <span>
                        <form method="POST" action="sendRating.">
                            
                            <c:choose>
                                <c:when test="${r.score==1}">
                                    <input type="radio" name="score" value="${r.score}" checked>1
                                    <input type="radio" name="score" value="${r.score}">2
                                    <input type="radio" name="score" value="${r.score}">3
                                    <input type="radio" name="score" value="${r.score}">4
                                    <input type="radio" name="score" value="${r.score}">5
                                </c:when>
                                <c:when test="${r.score==2}">
                                    <input type="radio" name="score" value="${r.score}">1
                                    <input type="radio" name="score" value="${r.score}" checked>2
                                    <input type="radio" name="score" value="${r.score}">3
                                    <input type="radio" name="score" value="${r.score}">4
                                    <input type="radio" name="score" value="${r.score}">5
                                </c:when>
                                <c:when test="${r.score==4}">
                                    <input type="radio" name="score" value="${r.score}">1
                                    <input type="radio" name="score" value="${r.score}">2
                                    <input type="radio" name="score" value="${r.score}" checked>3
                                    <input type="radio" name="score" value="${r.score}">4
                                    <input type="radio" name="score" value="${r.score}">5
                                </c:when>
                                <c:when test="${r.score==4}">
                                    <input type="radio" name="score" value="${r.score}">1
                                    <input type="radio" name="score" value="${r.score}">2
                                    <input type="radio" name="score" value="${r.score}">3
                                    <input type="radio" name="score" value="${r.score}" checked>4
                                    <input type="radio" name="score" value="${r.score}">5
                                </c:when>
                                <c:when test="${r.score==5}">
                                    <input type="radio" name="score" value="${r.score}">1
                                    <input type="radio" name="score" value="${r.score}">2
                                    <input type="radio" name="score" value="${r.score}">3
                                    <input type="radio" name="score" value="${r.score}">4
                                    <input type="radio" name="score" value="${r.score}" checked>5
                                </c:when>
                                <c:otherwise>
                                    <input type="radio" name="score">1
                                    <input type="radio" name="score">2
                                    <input type="radio" name="score">3
                                    <input type="radio" name="score">4
                                    <input type="radio" name="score">5
                                </c:otherwise>
                            </c:choose>
                            
                        </form>
                    </span>
		</li>
	</c:forEach>
</ul>
</body>
</html>