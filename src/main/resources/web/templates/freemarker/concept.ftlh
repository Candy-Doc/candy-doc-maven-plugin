<article class="concept">
    <section class="concept__information">
        <h1 class="concept__name">${concept.name}</h1>
        <label class="concept__fullName">${concept.className}</label>
        <p class="concept__description">${concept.description}</p>
    </section>
    <#if concept.interactsWith?has_content>
        <section class="interactions">
            <div class="interactions-box">
                <p class="interaction__title">${i18n.getString("INTERACTION")}</p>
                <#list concept.interactsWith as interaction>
                    <a href="${boundedContext.name}/${interaction.className}.html"
                       class="interaction">${interaction.name}</a>
                </#list>
            </div>
        </section>
    </#if>
</article>
