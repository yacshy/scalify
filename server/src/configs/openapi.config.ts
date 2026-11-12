import type { ElysiaOpenAPIConfig } from '@elysia/openapi';

export const OPENAPI_CONFIG: ElysiaOpenAPIConfig<boolean, '/docs'> = {
  documentation: {
    info: {
      title: 'scalify',
      version: '0.0.1'
    }
  },
  enabled: true,
  path: '/docs',
  provider: 'scalar',
  scalar: {
    _integration: 'elysiajs',
    default: false,
    defaultOpenAllTags: false,
    defaultOpenFirstTag: false,
    documentDownloadType: 'both',
    expandAllModelSections: false,
    expandAllParameters: false,
    expandAllResponses: false,
    expandAllSchemaProperties: false,
    hideClientButton: false,
    hideDarkModeToggle: true,
    hideModelNames: true,
    hideModels: false,
    hideSearch: false,
    hideTestRequestButton: false,
    isEditable: false,
    layout: 'classic',
    maxVisibleRequestBodyProperties: 12,
    modelsSectionLabel: 'Models',
    operationTitleSource: 'summary',
    orderRequiredPropertiesFirst: true,
    orderSchemaPropertiesBy: 'alpha',
    persistAuth: false,
    schemaKeyboardNav: false,
    showDeveloperTools: 'localhost',
    showOperationId: false,
    showSidebar: false,
    showToolbar: 'localhost',
    slug: 'scalify-api',
    telemetry: true,
    theme: 'elysiajs',
    title: 'scalify',
    withDefaultFonts: true
  }
};
