<%@include file="../../top.jsp" %> 
  
<h1>
	<c:choose>
	    <c:when test="${userId==null}">
	        Tous les films
	    </c:when>    
	    <c:otherwise>
	        Films de l'utilisateur ${userId}
	    </c:otherwise>
	</c:choose>
</h1>
<ul class="list-group">
	<c:forEach items="${movies}" var="movie">
		<li class="list-group-item">
			${movie.title}
			<ul>
				<c:forEach items="${movie.genres}" var="genre">
					<li>
						${genre.name}
					</li>
				</c:forEach>
			</ul>
		</li>
	</c:forEach>
</ul>
                                                
<%@include file="../../bottom.jsp" %>