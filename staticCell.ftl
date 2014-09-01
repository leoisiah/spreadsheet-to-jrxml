	<#macro staticCell cell>
			<staticText>
				<reportElement x="${cell.left}" y="${cell.top}" width="${cell.width}" height="${cell.height}"/>
				<box>
					<topPen lineWidth="${cell.borderTop}"/>
					<leftPen lineWidth="${cell.borderLeft}"/>
					<bottomPen lineWidth="${cell.borderBottom}"/>
					<rightPen lineWidth="${cell.borderRight}"/>
				</box>
				<textElement textAlignment="${cell.horizontalAlign}" verticalAlignment="${cell.verticalAlign}">
					<font size="8" <#if cell.isBold()>isBold="true"</#if>/>
				</textElement> 
				<text><![CDATA[${cell.text}]]></text>
			</staticText>
	</#macro>
