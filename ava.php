use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up() {
        Schema::create('games', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('user_id');
            $table->decimal('bet_amount', 10, 2);
            $table->decimal('multiplier', 5, 2)->default(1.00);
            $table->decimal('cashout_amount', 10, 2)->nullable();
            $table->boolean('status')->default(0); // 0 = in-progress, 1 = cashed out
            $table->timestamps();
        });
    }
    public function down() {
        Schema::dropIfExists('games');
    }
};

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Game extends Model {
    use HasFactory;
    
    protected $fillable = ['user_id', 'bet_amount', 'multiplier', 'cashout_amount', 'status'];
}

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Game;
use App\Models\User;
use Illuminate\Support\Facades\Auth;

class GameController extends Controller {
    public function startGame(Request $request) {
        $request->validate(['bet_amount' => 'required|numeric|min:1']);
        
        $user = Auth::user();
        if ($user->balance < $request->bet_amount) {
            return response()->json(['error' => 'Insufficient balance'], 400);
        }

        $game = Game::create([
            'user_id' => $user->id,
            'bet_amount' => $request->bet_amount,
            'multiplier' => 1.0
        ]);

        $user->balance -= $request->bet_amount;
        $user->save();

        return response()->json(['message' => 'Game started', 'game_id' => $game->id]);
    }

    public function updateMultiplier($gameId) {
        $game = Game::find($gameId);
        if (!$game || $game->status == 1) return response()->json(['error' => 'Game ended'], 400);
        
        $crashPoint = mt_rand(100, 300) / 100.0; // Random crash point (1.00 - 3.00)
        $multiplier = round($game->multiplier + 0.1, 2);

        if ($multiplier >= $crashPoint) {
            $game->status = 1; // Mark as crashed
            $game->save();
            return response()->json(['message' => 'Game crashed!', 'final_multiplier' => $multiplier]);
        }

        $game->multiplier = $multiplier;
        $game->save();

        return response()->json(['multiplier' => $multiplier]);
    }

    public function cashOut($gameId) {
        $game = Game::find($gameId);
        if (!$game || $game->status == 1) return response()->json(['error' => 'Game already ended'], 400);

        $user = Auth::user();
        $winnings = $game->bet_amount * $game->multiplier;
        $user->balance += $winnings;
        $user->save();

        $game->cashout_amount = $winnings;
        $game->status = 1;
        $game->save();

        return response()->json(['message' => 'Cashed out!', 'winnings' => $winnings]);
    }

    public function gameHistory() {
        $games = Game::where('user_id', Auth::id())->latest()->take(10)->get();
        return response()->json($games);
    }
}
use App\Http\Controllers\GameController;
use Illuminate\Support\Facades\Route;

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/fast-api/start', [GameController::class, 'startGame']);
    Route::get('/fast-api/multiplier/{gameId}', [GameController::class, 'updateMultiplier']);
    Route::post('/fast-api/cashout/{gameId}', [GameController::class, 'cashOut']);
    Route::get('/fast-api/history', [GameController::class, 'gameHistory']);
});

use Laravel\Sanctum\HasApiTokens;
class User extends Authenticatable {
    use HasApiTokens, HasFactory, Notifiable;
}
protected $middlewareGroups = [
    'api' => [
        \Laravel\Sanctum\Http\Middleware\EnsureFrontendRequestsAreStateful::class,
        'throttle:api',
        \Illuminate\Routing\Middleware\SubstituteBindings::class,
    ],
];
