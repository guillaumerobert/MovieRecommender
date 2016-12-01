<%@include file="../../top.jsp" %>

        <h1>
	    Movies recommendations
       </h1>
            
        <ul class="list-group">
            <c:forEach items="${recommendations}" var="r">
                <li class="list-group-item">${r.movie.title} - ${r.score} / 5</li>
            </c:forEach>
        </ul>
                
<%@include file="../../bottom.jsp" %>