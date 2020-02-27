export interface ClassMetricValue1 {
  numberOfPackageImportsIncoming: number;
  numberOfPackageImportsOutgoing: number;

  numberOfAttributeInvocationsIncoming: number;
  numberOfAttributeInvocationsOutgoing: number;

  numberOfMethodInvocationsIncoming: number;
  numberOfMethodInvocationsOutgoing: number;

  numberOfConstructorInvocationsIncoming: number;
  numberOfConstructorInvocationsOutgoing: number;

  bidirectionalNumberOfPackageImports: number;

  bidirectionalNumberOfAttributeInvocations: number;

  bidirectionalNumberOfMethodInvocations: number;

  bidirectionalNumberOfConstructorInvocations: number;
}
