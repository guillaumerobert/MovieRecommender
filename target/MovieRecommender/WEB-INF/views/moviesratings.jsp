<%@include file="../../top.jsp" %>
<h1>
	<c:choose>
	    <c:when test="${userId==null}">
	        Aucune note / TO FIX REDIRECT POST
	    </c:when> 
	    <c:otherwise>
	        Notes de l'utilisateur ${userId} pour les films
	    </c:otherwise>
	</c:choose>
</h1>
<ul class="list-group">
	<c:forEach items="${ratings}" var="r">
		<li class="list-group-item">
                    <span>${r.movie.title}</span>
                    <span>
                        <form method="POST" action="/MovieRecommender/moviesratings?user_id=${userId}">
                            
                            <input type="number" name="userId" value="${userId}" hidden="hidden">
                            <input type="number" name="movieId" value="${r.movie.id}" hidden="hidden">
                            <input type="radio" name="note" onclick="this.form.submit();" value="1" <c:if test="${r.score == 1}">checked</c:if>>
                            <input type="radio" name="note" onclick="this.form.submit();" value="2" <c:if test="${r.score == 2}">checked</c:if>>
                            <input type="radio" name="note" onclick="this.form.submit();" value="3" <c:if test="${r.score == 3}">checked</c:if>>
                            <input type="radio" name="note" onclick="this.form.submit();" value="4" <c:if test="${r.score == 4}">checked</c:if>>
                            <input type="radio" name="note" onclick="this.form.submit();" value="5" <c:if test="${r.score == 5}">checked</c:if>>
                            
                        </form>
                    </span>
		</li>
	</c:forEach>
</ul>

<%@include file="../../bottom.jsp" %>