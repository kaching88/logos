package com.kaching.logos.enums

sealed trait Level

case object INFO extends Level

case object WARN extends Level

case object DEBUG extends Level

case object ERROR extends Level