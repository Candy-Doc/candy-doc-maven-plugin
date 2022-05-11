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
            <h1 class="concept__name">${concept.simpleName}</h1>
            <span class="concept__class-name">${concept.canonicalName}</span>
            <p class="concept__description">${concept.description}</p>
        </section>
        <#if concept.interactsWith?has_content>
            <section class="interactions">
                <div class="interactions-box">
                    <h2 class="interaction__title">${i18n.getString("INTERACTION")}</h2>
                    <#list concept.interactsWith as interaction>
                        <a href="${boundedContext.simpleName}/${interaction.canonicalName}.html"
                           class="interaction">${interaction.simpleName}</a>
                    </#list>
                </div>
            </section>
        </#if>
    </article>
</main>
</body>
</html>
