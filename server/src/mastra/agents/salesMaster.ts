import { Agent } from '@mastra/core/agent'
import { AgentID, AgentInstruction, AgentName, Model } from '@/configs/agent.config'

export const salesMasterAgent = new Agent({
  id: AgentID.SalesMaster,
  instructions: AgentInstruction.SalesMaster,
  model: Model,
  name: AgentName.SalesMaster
})
