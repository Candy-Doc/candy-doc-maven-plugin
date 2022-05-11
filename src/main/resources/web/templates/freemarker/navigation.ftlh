<aside class="navigation">
    <header class="project-name">
        <a href="index.html">CANDY-DOC</a>
    </header>
    <nav class="project-navigation">
        <#list boundedContexts as boundedContext>
            <ul class="bounded-contexts">
                <li class="bounded-context">
                    <a href="${boundedContext.simpleName}/${boundedContext.simpleName}.html">${boundedContext.simpleName}</a>
                    <ul class="bounded-context__concepts">
                        <#if boundedContext.getConcepts()?has_content>
                            <#list boundedContext.getConceptTypes() as conceptType>
                                <#if boundedContext.getConcepts(conceptType)?has_content>
                                    <li class="concept-type">${i18n.getString(conceptType.name())}</li>
                                    <ul class="concepts">
                                        <#list boundedContext.getConcepts(conceptType) as concept>
                                            <li class="concepts__item">
                                                <a href="${boundedContext.simpleName}/${concept.canonicalName}.html">${concept.simpleName}</a>
                                            </li>
                                        </#list>
                                    </ul>
                                </#if>
                            </#list>
                        </#if>
                    </ul>
                </li>
            </ul>
        </#list>
    </nav>
</aside>
