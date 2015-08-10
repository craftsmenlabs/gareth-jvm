grammar Gareth;

experiment: 'Experiment:' expirementName=GlueLine;
baseline: 'Baseline:' baselineGlueLine=GlueLine;
assumption: 'Assumption:' assumptionGlueLine=GlueLine;
time: 'Time:' timeGlueLine=GlueLine;
success: 'Success:' successGlueLine=GlueLine;
failure: 'Failure:' failureGlueLine=GlueLine;


experimentBlock:
    (NL)*
    (experiment NL)
    (NL)*
    (assumptionBlock)+
    EOF;

successOrFailure:
    (((NL)+ success NL failure)
    | ((NL)+ failure NL success)
    | ((NL)+ success)
    | ((NL)+ failure));

assumptionBlock:
    ((NL)+ baseline)
    ((NL)+ assumption)
    ((NL)+ time)
    (successOrFailure)?
    (NL)*;

GlueLine: ('A'..'Z' | 'a'..'z' | ' ' | '0'..'9' | '%' | '-' | '(' | ')' )+;

WS: (' ' | '\t')+;
NL:  '\r'? '\n';


NLOREOF: (NL | EOF);