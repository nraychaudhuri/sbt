package sbt

	import Def.Setting
	import java.net.URI

private[sbt] final class GroupedAutoPlugins(val all: Seq[AutoPlugin], val byBuild: Map[URI, Seq[AutoPlugin]])
{
	def globalSettings: Seq[Setting[_]] = all.flatMap(_.globalSettings)
	def buildSettings(uri: URI): Seq[Setting[_]] = byBuild.getOrElse(uri, Nil).flatMap(_.buildSettings)
}

private[sbt] object GroupedAutoPlugins
{
	private[sbt] def apply(units: Map[URI, LoadedBuildUnit]): GroupedAutoPlugins =
	{
		val byBuild: Map[URI, Seq[AutoPlugin]] = units.mapValues(unit => unit.defined.values.flatMap(_.autoPlugins).toSeq.distinct).toMap
		val all: Seq[AutoPlugin] = byBuild.values.toSeq.flatten.distinct
		new GroupedAutoPlugins(all, byBuild)
	}
}