import { Mastra } from '@mastra/core'
import { hairCareMasterAgent, salesMasterAgent } from './agents'

export const mastra = new Mastra({
  agents: {
    hairCareMasterAgent,
    salesMasterAgent
  }
})
