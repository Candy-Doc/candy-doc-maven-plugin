<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CandyDoc</title>
    <link rel="stylesheet" href="../style.css">
    <base href="file://${baseFolder}/">
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
                <#if boundedContext.getConcepts()?has_content>
                    <#list boundedContext.getConceptTypes() as conceptType>
                        <#if boundedContext.getConcepts(conceptType)?has_content>
                            <li class="concepts__item concept__name">${i18n.getString(conceptType.name())}</li>
                            <#list boundedContext.getConcepts(conceptType) as concept>
                                <li class="concepts__item concept--simple-concept">
                                    <a href="${boundedContext.name}/${concept.fullName}.html">${concept.name}</a>
                                </li>
                            </#list>
                        </#if>
                    </#list>
                </#if>
            </ul>
        </#list>
    </nav>
</div>
<article class="concept">
    <section class="concept__information">
        <h1 class="concept__name">${concept.name}</h1>
        <label class="concept__fullName">${concept.fullName}</label>
        <p class="concept__description">${concept.description}</p>
    </section>
    <#if concept.interactsWith?has_content>
        <section class="interactions">
            <div class="interactions-box">
                <p class="interaction__title">${i18n.getString("INTERACTION")}</p>
                <#list concept.interactsWith as interaction>
                    <a href="${boundedContext.name}/${interaction.fullName}.html"
                       class="interaction">${interaction.simpleName}</a>
                </#list>
            </div>
        </section>
    </#if>
</article>
</body>
</html>