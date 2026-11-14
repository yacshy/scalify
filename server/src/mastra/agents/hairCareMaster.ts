import { Agent } from '@mastra/core/agent'
import { AgentID, AgentInstruction, AgentName, Model } from '@/configs/agent.config'

export const hairCareMasterAgent = new Agent({
  id: AgentID.HairCareMaster,
  instructions: AgentInstruction.HairCareMaster,
  model: Model,
  name: AgentName.HairCareMaster
})
