const RPC = require('discord-rpc');

const clientId = '1394002096562638960';

const rpc = new RPC.Client({ transport: 'ipc' });

rpc.on('ready', () => {
    console.log('Discord RPC ready');

    rpc.setActivity({
        details: 'VWeb OMXium',
        state: 'v.0.0.4-Alpha',
        startTimestamp: Math.floor(Date.now() / 1000),
        largeImageKey: 'vweb',
        largeImageText: 'VWeb OMXium (GMP Protocol)',
        instance: false,
    });
});

rpc.login({ clientId }).catch(console.error);

setInterval(() => {}, 1000);