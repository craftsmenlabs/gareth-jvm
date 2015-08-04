grammar Gareth;

experiment: 'Experiment:' expirementName=GlueLine;
baseline: 'Baseline:' baselineGlueLine=GlueLine;
assumption: 'Assumption:' assumptionGlueLine=GlueLine;
time: 'Time:' timeGlueLine=GlueLine;
success: 'Success:' successGlueLine=GlueLine;
failure: 'Failure:' failureGlueLine=GlueLine;


experimentBlock:
    (experiment NL)
    (NL)*
    (assumptionBlock)+
    EOF;

assumptionBlock:
    (NL baseline)
    (NL assumption)
    (NL time)
    (NL success)?
    (NL failure)?
    (NL)*;

GlueLine: ('A'..'Z' | 'a'..'z' | ' ' | '0'..'9' | '%' | '-' | '(' | ')' )+;

WS: (' ' | '\t')+;
NL:  '\r'? '\n';

NLOREOF: (NL | EOF);