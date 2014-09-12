<#import "staticCell.ftl" as s>
<#import "detailCell.ftl" as d>
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="portrait" language="groovy" pageWidth="595" pageHeight="842" whenNoDataType="AllSectionsNoDetail" columnWidth="555" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20">
	<property name="ireport.zoom" value="1.0"/>
	<property name="ireport.x" value="0"/>
	<property name="ireport.y" value="0"/>
	<background>
		<band splitType="Stretch"/>
	</background>
	<title>	
		<band height="${reportDomain.titleHeight}" splitType="Stretch">
		<#list reportDomain.titleCells as cell>
			<@s.staticCell cell/>
		</#list>			
		</band>
	</title>
	<pageHeader>
		<band height="0" splitType="Stretch"/>
	</pageHeader>
	<columnHeader>
		<band height="${reportDomain.columnHeaderHeight}" splitType="Stretch">
		<#list reportDomain.columnHeaderCells as cell>
			<@s.staticCell cell/>
		</#list>					
		</band>
	</columnHeader>
	<detail>
		<band height="${reportDomain.detailHeight}" splitType="Stretch">
		<#list reportDomain.detailCells as cell>
			<@d.detailCell cell/>
		</#list>					
		</band>
	</detail>
	<columnFooter>
		<band height="0" splitType="Stretch"/>
	</columnFooter>
	<pageFooter>
		<band height="0" splitType="Stretch"/>
	</pageFooter>
	<summary>
		<band height="${reportDomain.summaryHeight}" splitType="Stretch">
		<#list reportDomain.summaryCells as cell>
			<@s.staticCell cell/>
		</#list>							
		</band>
	</summary>
</jasperReport>
