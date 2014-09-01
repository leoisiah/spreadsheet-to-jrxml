	<#macro detailCell cell>
			<textField isStretchWithOverflow="true">
				<reportElement stretchType="RelativeToTallestObject" x="${cell.left}" y="${cell.top}" width="${cell.width}" height="${cell.height}"/>
				<textElement textAlignment="${cell.horizontalAlign}" verticalAlignment="${cell.verticalAlign}">
					<font size="8" <#if cell.isBold()>isBold="true"</#if>/>
				</textElement> 				
				<textFieldExpression class="java.lang.String"><![CDATA[]]></textFieldExpression>
			</textField>		
	</#macro>
