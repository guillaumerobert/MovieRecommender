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
	<c:forEach items="${map}" var="m">
		<li>
			${m.key.title} : ${m.value}
		</li>
	</c:forEach>
</ul>
</body>
</html>