<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CandyDoc</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="navigation">
    <h1 class="project-name">
        <a href="index.html">CANDY-DOC</a>
    </h1>
    <nav class="concepts">
        <#list boundedContexts as boundedContext>
            <ul class="concepts-list">
                <li class="concepts__item concept--bounded-context">
                    <a href="${boundedContext.name}/${boundedContext.name}.html">${boundedContext.name}</a>
                </li>
                <#if boundedContext.coreConcepts?has_content>
                    <li class="concepts__item concept__name">Core concepts:</li>
                    <#list boundedContext.coreConcepts as coreConcept>
                        <li class="concepts__item concept--simple-concept">
                            <a href="${boundedContext.name}/${coreConcept.fullName}.html">${coreConcept.className}</a>
                        </li>
                    </#list>
                </#if>
                <#if boundedContext.valueObjects?has_content>
                    <li class="concepts__item concept__name">Value objects:</li>
                    <#list boundedContext.valueObjects as valueObject>
                        <li class="concepts__item concept--simple-concept">
                            <a href="${boundedContext.name}/${valueObject.fullName}.html">${valueObject.className}</a>
                        </li>
                    </#list>
                </#if>
                <#if boundedContext.domainEvents?has_content>
                    <li class="concepts__item concept__name">Domain Events:</li>
                    <#list boundedContext.domainEvents as domainEvent>
                        <li class="concepts__item concept--simple-concept">
                            <a href="${boundedContext.name}/${domainEvent.fullName}.html">${domainEvent.className}</a>
                        </li>
                    </#list>
                </#if>
                <#if boundedContext.domainCommands?has_content>
                    <li class="concepts__item concept__name">Domain Commands:</li>
                    <#list boundedContext.domainCommands as domainCommand>
                        <li class="concepts__item concept--simple-concept">
                            <a href="${boundedContext.name}/${domainCommand.fullName}.html">${domainCommand.className}</a>
                        </li>
                    </#list>
                </#if>
            </ul>
        </#list>
    </nav>
</div>
</body>
</html>