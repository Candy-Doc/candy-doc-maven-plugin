<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CandyDoc</title>
    <link rel="stylesheet" href="../style.css">
    <base href="file://${baseFolder}/">
</head>
<body>
<#include "./navigation.ftlh">
<main>
    <article class="concept">
        <section class="concept__information">
            <h1 class="concept__name">${boundedContext.simpleName}</h1>
            <label class="concept__class-name">${boundedContext.packageName}</label>
            <p class="concept__description">${boundedContext.description}</p>
        </section>
        <#if boundedContext.getConcepts()?has_content>
            <section class="interactions">
                <#list boundedContext.getConceptTypes() as conceptType>
                    <#if boundedContext.getConcepts(conceptType)?has_content>
                        <div class="interactions-box">
                            <h2 class="interaction__title">${i18n.getString(conceptType.name())}</h2>
                            <#list boundedContext.getConcepts(conceptType) as concept>
                                <a href="${boundedContext.simpleName}/${concept.canonicalName}.html"
                                   class="interaction">
                                    ${concept.simpleName}
                                </a>
                            </#list>
                        </div>
                    </#if>
                </#list>
            </section>
        </#if>
    </article>
</main>
</body>
</html>
