<article class="concept">
    <section class="concept__information">
        <h1 class="concept__name">${concept.className}</h1>
        <label class="concept__fullName">${concept.fullName}</label>
        <p class="concept__description">${concept.description}</p>
    </section>
    <#if concept.interactsWith?has_content>
        <section class="interactions">
            <div class="interactions-box">
                <p class="interaction__title">Interactions:</p>
                <#list concept.interactsWith as interaction>
                    <a href="${boundedContext.name}/${interaction.fullName}.html"
                       class="interaction">${interaction.simpleName}</a>
                </#list>
            </div>
        </section>
    </#if>
</article>