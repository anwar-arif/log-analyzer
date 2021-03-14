package com.loganalyzer.Enums

object StatusEnum extends Enumeration {
  type Status = Value
  val Okay = Value("Okay")
  val InProgress = Value("In Progress")
  val Failed = Value("Failed")
}
