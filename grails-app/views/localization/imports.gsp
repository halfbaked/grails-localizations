<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="localization.imports" default="Import Properties File" /></title>
        <g:localizationHelpBalloons />
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/', absolute: true)}"><g:message code="home" default="Home" /></a></span>
            <g:localizationMenuButton/>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="localization.list" default="Localization List" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="localization.imports" default="Import Properties File" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:form action="load" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="file"><g:message code="localization.imports.file" default="Properties file" />:</label>
                                </td>
                                <td valign="top" class="value">
                                    <g:select name="file" from="${names}"/>&nbsp;<g:localizationHelpBalloon code="localization.imports.file" />
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="${message(code:'localization.imports.import', 'default':'Import')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
