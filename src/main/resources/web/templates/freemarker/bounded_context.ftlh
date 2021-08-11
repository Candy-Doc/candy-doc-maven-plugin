<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CandyDoc</title>
    <link rel="stylesheet" href="../style.css">
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
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
    <section id="concept__graph"></section>
    <div class="concept__information">
        <section class="concept__details">
            <h1 class="concept__name">${boundedContext.name}</h1>
            <label class="concept__fullName">${boundedContext.packageName}</label>
            <p class="concept__description">${boundedContext.description}</p>
        </section>
        <#if boundedContext.getConcepts()?has_content>
            <section class="interactions">
                <#list boundedContext.getConceptTypes() as conceptType>
                    <#if boundedContext.getConcepts(conceptType)?has_content>
                        <div class="interactions-box">
                            <p class="interaction__title">${i18n.getString(conceptType.name())}</p>
                            <#list boundedContext.getConcepts(conceptType) as concept>
                                <a href="${boundedContext.name}/${concept.fullName}.html" class="interaction">
                                    ${concept.name}
                                </a>
                            </#list>
                        </div>
                    </#if>
                </#list>
            </section>
        </#if>
    </div>
</article>
</body>
</html>

<script>

    createNetwork()

    function createNetwork() {
        var nodes = new vis.DataSet([
            <#if boundedContext.getConcepts()?has_content>
                <#list boundedContext.getConceptTypes() as conceptType>
                    <#if boundedContext.getConcepts(conceptType)?has_content>
                        <#list boundedContext.getConcepts(conceptType) as concept>
                            {
                                id: "${concept.fullName}",
                                label: "${concept.name}",
                                group: "${i18n.getString(conceptType.name())}",
                                title: "${concept.name} \n ${concept.description}",
                                description: "${concept.description}"
                            },
                        </#list>
                    </#if>
                </#list>
            </#if>

        ]);
        var edges = new vis.DataSet([

            <#if boundedContext.getConcepts()?has_content>
                <#list boundedContext.getConceptTypes() as conceptType>
                    <#if boundedContext.getConcepts(conceptType)?has_content>
                        <#list boundedContext.getConcepts(conceptType) as concept>
                            <#list concept.interactsWith as interaction>
                                {from: "${concept.fullName}", to: "${interaction.fullName}"},
                            </#list>
                        </#list>
                    </#if>
                </#list>
            </#if>

        ]);
        var data = {nodes: nodes, edges: edges};
        var container = document.getElementById("concept__graph");
        var options = {
            autoResize: false,
            height: '100%',
            width: '100%',
            locale: 'en',
            groups: {
                ValueObject: {color: {background: '#FA8072', border: '#FA8072'}, borderWidth: 3},
                CoreConcept: {color: {background: '#BCD9C3', border: '#BCD9C3'}, borderWidth: 3},
                DomainEvent: {color: {background: '#35C3D4', border: '#35c3d4'}, borderWidth: 3},
                Aggregate:   {color: {background: '#747A0C', border: '#747A0C'}, borderWidth: 3},
                DomainCommand: {color: {background: '#B735D4', border: '#B735D4'}, borderWidth: 3}
            },
            clickToUse: false,
            physics: {
                enabled: false
            },
            edges: {
                arrows: 'to',
                font: '12px arial #ff0000',
                scaling: {
                    label: true,
                },
                color: {
                    highlight: 'red'
                },
                shadow: true,
                smooth: true,
            },
            nodes: {
                color: {
                    border: '#FFF',
                    highlight: {
                        border: '#FF5733',
                        background: '#FF5733'
                    },
                },
                font: {
                    color: '#FFF',
                }
            }
        };
        var network = new vis.Network(
            container,
            data,
            options);

        adjustNetworkHeight(network, nodes, container)

    }

    function adjustNetworkHeight(network, nodes, container) {
        let minHeight = Number.MAX_SAFE_INTEGER
        let maxHeight = Number.MIN_SAFE_INTEGER

        nodes.forEach(node => {
            const boundingBox = network.getBoundingBox(node.id)
            if(boundingBox.top < minHeight)
                minHeight = boundingBox.top
            if(boundingBox.bottom > maxHeight)
                maxHeight = boundingBox.bottom
        })

        const heightOffset = 100
        const naturalHeight = maxHeight - minHeight + heightOffset

        container.style.height = network.getScale() * naturalHeight + 'px'
        network.redraw()
        network.fit()
    }
</script>