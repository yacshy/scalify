export const Model = 'openai/gpt-6.1-sol'

export enum AgentID {
  SalesMaster = 'sales-master-agent',
  HairCareMaster = 'hair-care-master-agent'
}

export enum AgentName {
  SalesMaster = 'Sales Master Agent',
  HairCareMaster = 'Hair Care Master Agent'
}

export const AgentInstruction = {
  HairCareMaster: 'You are a hair care master assistant.',
  SalesMaster: 'You are a sales master assistant.'
}
