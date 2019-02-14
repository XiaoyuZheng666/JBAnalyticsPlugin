//
//  JBUserDefaults.m
//  黄金象
//
//  Created by MAC005 on 2019/1/18.
//

#import "JBUserDefaults.h"
#import "SBJson.h"

#define kUserDefault_Launch @"UserDefault_Launch"


@implementation JBUserDefaults

+(NSMutableArray *)getFailedLaunchs{
    NSString *json = [self getSeting:kUserDefault_Launch];
    NSArray *datas = [json JSONValue];
    json = nil;
    return [NSMutableArray arrayWithArray:datas];
}

+(void)setFailedLaunchs:(NSMutableArray *)datas{
    if (datas) {
        NSString *json = [datas JSONRepresentation];
        [self setSeting:kUserDefault_Launch Value:json];
        json = nil;
    }

}

+(NSString *)getSeting:(NSString*)key{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *value=[defaults objectForKey:key];
    if (!value) {
        value = @"";
    }
    defaults = nil;
    return value;
}

+(void)setSeting:(NSString *)key Value:(NSString*)value{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:value forKey:key];
    [defaults synchronize];
    defaults = nil;
}


@end
