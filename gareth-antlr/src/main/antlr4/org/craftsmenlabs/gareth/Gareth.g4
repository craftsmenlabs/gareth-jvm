grammar Gareth;

experiment: 'Experiment:' expirementName=GlueLine;
weight: 'Weight:' experimentWeight=GlueLine;
baseline: 'Baseline:' baselineGlueLine=GlueLine;
assume: 'Assumption:' assumptionGlueLine=GlueLine;
time: 'Time:' timeGlueLine=GlueLine;
success: 'Success:' successGlueLine=GlueLine;
failure: 'Failure:' failureGlueLine=GlueLine;


experimentBlock:
    (NL)*
    (experiment NL)
    (weight NL)?
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
    ((NL)+ assume)
    ((NL)+ time)
    (successOrFailure)?
    (NL)*;

GlueLine: ('A'..'Z' | 'a'..'z' | ' ' | '0'..'9' | '%' | '-' | '(' | ')' )+;

WS: (' ' | '\t')+;
NL:  '\r'? '\n';


NLOREOF: (NL | EOF);