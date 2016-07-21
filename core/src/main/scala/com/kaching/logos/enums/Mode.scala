package com.kaching.logos.enums

sealed trait Mode

case object BEFORE extends Mode

case object AFTER extends Mode

case object AROUND extends Mode