//
//  CDVXYProgress.m
//  HelloCordova
//
//  Created by MAC005 on 2018/4/13.
//

#import "JBAnalyticsPlugin.h"
#import "JBAnalytics.h"
@implementation JBAnalyticsPlugin
- (void)onEventWithData:(CDVInvokedUrlCommand*)command{
NSString *eventId = [command.arguments objectAtIndex:0];
    if (eventId == nil || [eventId isKindOfClass:[NSNull class]]) {
        return;
    }
    NSDictionary *parameters = [command.arguments objectAtIndex:1];
    if (parameters == nil && [parameters isKindOfClass:[NSNull class]]) {
        parameters = nil;
    }

    [JBAnalytics reportSucceededEventwithId:eventId andDataDic:parameters];

}


@end
