<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CandyDoc</title>
    <link rel="stylesheet" href="../style.css">
    <base href="file://${baseFolder}/">
</head>
<body>
${navigation ?no_esc}
<article class="concept">
    <section class="concept__information">
        <h1 class="concept__name">${boundedContext.name}</h1>
        <label class="concept__fullName">${boundedContext.name}</label>
        <p class="concept__description">${boundedContext.description}</p>
    </section>
    <#if boundedContext.coreConcepts?has_content || boundedContext.valueObjects?has_content|| boundedContext.domainEvents?has_content|| boundedContext.domainCommands?has_content>
        <section class="interactions">
            <#if boundedContext.coreConcepts?has_content>
                <div class="interactions-box">
                    <p class="interaction__title">Core Concepts:</p>
                    <#list boundedContext.coreConcepts as coreConcept>
                        <a href="${boundedContext.name}/${coreConcept.fullName}.html" class="interaction">
                            ${coreConcept.className}
                        </a>
                    </#list>
                </div>
            </#if>
            <#if boundedContext.valueObjects?has_content>
                <div class="interactions-box">
                    <p class="interaction__title">Value Objects:</p>
                    <#list boundedContext.valueObjects as valueObject>
                        <a href="${boundedContext.name}/${valueObject.fullName}.html" class="interaction">
                            ${valueObject.className}
                        </a>
                    </#list>
                </div>
            </#if>
            <#if boundedContext.domainEvents?has_content>
                <div class="interactions-box">
                    <p class="interaction__title">Domain Events:</p>
                    <#list boundedContext.domainEvents as domainEvent>
                        <a href="${boundedContext.name}/${domainEvent.fullName}.html" class="interaction">
                            ${domainEvent.className}
                        </a>
                    </#list>
                </div>
            </#if>
            <#if boundedContext.domainCommands?has_content>
                <div class="interactions-box">
                    <p class="interaction__title">Domain Commands:</p>
                    <#list boundedContext.domainCommands as domainCommand>
                        <a href="${boundedContext.name}/${domainCommand.fullName}.html" class="interaction">
                            ${domainCommand.className}
                        </a>
                    </#list>
                </div>
            </#if>
        </section>
    </#if>
</article>
</body>
</html>