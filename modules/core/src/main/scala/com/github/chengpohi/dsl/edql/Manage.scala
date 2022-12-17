package com.github.chengpohi.dsl.edql

/**
 * eql
 * Created by chengpohi on 6/26/16.
 */
trait Manage extends Delete with QueryEDQL {

  case object node {
  }

  case object indice {
  }

  case object cluster {
  }

  case object create {
    def analyze(analyzer: String): AnalyzerDefinition =
      AnalyzerDefinition(analyzer)

    def field(field: String): FieldDefinition = FieldDefinition(field)
  }

  case object add {
  }

  case object restore {
  }

  case object close {
  }

  case object open {
  }

  case object pending {
  }

  case object waiting {
  }

  case object refresh {
  }

  case object cat {
    def nodes: CatNodesDefinition = {
      CatNodesDefinition()
    }

    def allocation: CatAllocationDefinition = {
      CatAllocationDefinition()
    }

    def master: CatMasterDefinition = {
      CatMasterDefinition()
    }

    def indices: CatIndicesDefinition = {
      CatIndicesDefinition()
    }

    def shards: CatShardsDefinition = {
      CatShardsDefinition()
    }

    def count: CatCountDefinition = {
      CatCountDefinition()
    }

    def pending_tasks: CatPendingTaskDefinition = {
      CatPendingTaskDefinition()
    }

    def recovery: CatRecoveryDefinition = {
      CatRecoveryDefinition()
    }
  }
}
